package com.bilibackend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bilibackend.config.OssConfig;
import com.bilibackend.service.OssService;
import com.bilibackend.utils.UploadParam;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/13 23:36
 * @Version 1.0
 */
@Service
public class OssServiceImpl implements OssService {


    //todo 可以直接用MD5作为文件名的
    @Autowired
    private OssConfig ossConfig;


    @Autowired
    private COSClient cosClient;


    @Autowired
    private TransferManager transferManager;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 已上传文件的md5列表
     */
    private static final String MD5_KEY = "upload:file:md5List";

    /**
     * 还是以MD5为key，第一个元素为uploadId
     */
    private static final String TAG_KEY = "upload:file:tags:";

    private final ConcurrentHashMap<String, File> tempFile = new ConcurrentHashMap<>();


    @Override
    public String uploadCheck(String md5) {
        return (String) redisTemplate.boundHashOps(MD5_KEY).get(md5);
    }

    private String initMultipartUpload(String key, String bucketName, String md5) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setHeader("Cache-Control", 60 * 60 * 3);
        objectMetadata.setCacheControl("public,max-age=10800");
        objectMetadata.setContentMD5(md5);
        request.setObjectMetadata(objectMetadata);

        try {
            InitiateMultipartUploadResult initResult = cosClient.initiateMultipartUpload(request);
            return initResult.getUploadId();
        } catch (CosClientException e) {
            throw e;
        }

    }

    @Override
    public String partUpload(HttpServletRequest req) {
        //todo 断点续传，如果失败，先查询redis中已经缓存的索引，然后从下一个位置开始上传
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
        //文件分片数据
        MultipartFile toFile = multipartRequest.getFile("file");
        if (ObjectUtil.isNull(toFile)) {
            return null;
        }
        int index = Integer.parseInt(multipartRequest.getParameter("index"));
        String md5 = multipartRequest.getParameter("md5");
        // 总片数
        int total = Integer.parseInt(multipartRequest.getParameter("total"));

        String fileName = multipartRequest.getParameter("name");

        File file = null;
        long size = 0;
        try {
            file = transferToFile(toFile, fileName, index);
            size = file.length();
        } catch (IOException e) {
            //出错的时候移除临时文件
            removeTemplate(fileName);
            throw new RuntimeException(e);
        }

        if (ObjectUtil.isNull(file)) {
            return null;
        }

        //第一次上传
        if (index == 1) {
            String name = IdUtil.randomUUID();
            String fileType = FileTypeUtil.getType(file);

            String fileKey;
            if (fileType.contains("mp4")) {
                fileKey = "video/" + name + "." + fileType;
            } else {
                fileKey = "pic/" + name + "." + fileType;
            }
            //将uploadId缓存起来，后面继续放入tags，用于合并的时候

            String uploadId = this.initMultipartUpload(fileKey, ossConfig.getBucketName(), md5);

            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .file(file)
                    .index(index)
                    .key(fileKey).build();

            PartETag partETag = this.uploadPart(build);
            // 第一个放的是uploadId,第二个是文件名
            int partNumber = partETag.getPartNumber();
            String eTag = partETag.getETag();
            List<String> range = (List<String>) (List<?>) redisTemplate.opsForList().range(TAG_KEY + md5, 0, -1);
            if (CollectionUtil.isEmpty(range)) {
                redisTemplate.opsForList().rightPushAll(TAG_KEY + md5, uploadId, fileKey, partNumber + "--" + eTag);
            } else {
                redisTemplate.opsForList().rightPush(TAG_KEY + md5, partNumber + "--" + eTag);
            }
            removeTemplate(fileName);
            return "ok";
        } else if (index < total) {
            //中间的
            List<Serializable> range = redisTemplate.opsForList().range(TAG_KEY + md5, 0, -1);
            if (range == null) {
                return null;
            }
            String uploadId = (String) range.get(0);
            String fileKey = (String) range.get(1);

            //todo 断线续传
            if (range.size() >= index + 2) {
                return "ok";
            }

            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .file(file)
                    .index(index)
                    .key(fileKey).build();
            PartETag partETag = this.uploadPart(build);

            int partNumber = partETag.getPartNumber();
            String eTag = partETag.getETag();
            redisTemplate.opsForList().rightPush(TAG_KEY + md5, partNumber + "--" + eTag);
            removeTemplate(fileName);
            return "ok";
        } else {
            //最后一个
            List<Serializable> range = redisTemplate.opsForList().range(TAG_KEY + md5, 0, -1);
            if (range == null) {
                return null;
            }
            String uploadId = (String) range.get(0);
            String fileKey = (String) range.get(1);

            List<PartETag> partETags = new ArrayList<>();


            List<String> list = (List<String>) (List<?>) range.subList(2, range.size());


            list.forEach(item -> {
                String s = item;
                if (item.contains("--")) {
                    String[] split = s.split("--");
                    int i = Integer.parseInt(split[0]);
                    PartETag partETag = new PartETag(Integer.parseInt(split[0]), split[1]);
                    partETags.add(partETag);
                }
            });
            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .file(file)
                    .index(index)
                    .key(fileKey).build();
            PartETag partETag = this.uploadPart(build);
            partETags.add(partETag);
            String b = this.completeMultipartUpload(partETags, fileKey, uploadId, md5, fileName);
            if (ObjectUtil.isNotNull(b)) {
                String url = getFormatKey(fileKey);
                redisTemplate.expire(TAG_KEY + md5, 5, TimeUnit.SECONDS);
                redisTemplate.opsForHash().put(MD5_KEY, md5, url);

                removeTemplate(fileName);
                return url;
            }
            removeTemplate(fileName);
            return null;
        }
    }

    @Override
    public String transferManagerUpload(HttpServletRequest req) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
        //文件分片数据
        MultipartFile file = multipartRequest.getFile("file");
        String md5 = multipartRequest.getParameter("md5");
        if (ObjectUtil.isNull(file)) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (ObjectUtil.isNull(inputStream)) {
            return null;
        }

        String name = IdUtil.randomUUID();
        String fileType = FileTypeUtil.getType(inputStream);

        String fileKey;
        if (fileType.contains("mp4")) {
            fileKey = "video/" + name + "." + fileType;
        } else {
            fileKey = "pic/" + name + "." + fileType;
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setHeader("Cache-Control", 60 * 60 * 3);
        objectMetadata.setCacheControl("public,max-age=10800");

        objectMetadata.setContentMD5(md5);

        //设置长度是为了便于分块
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), fileKey, inputStream, objectMetadata);

        putObjectRequest.setStorageClass(StorageClass.Standard_IA);

        try {
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回 UploadResult, 失败抛出异常
            Upload upload = transferManager.upload(putObjectRequest);

            //todo 用upload可以获取上传进度
            UploadResult uploadResult = upload.waitForUploadResult();
            String url = getFormatKey(fileKey);
            redisTemplate.opsForHash().put(MD5_KEY, md5, url);
            return fileKey;
        } catch (CosClientException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    private PartETag uploadPart(UploadParam uploadParam) {

        UploadPartRequest uploadPartRequest = new UploadPartRequest();

        uploadPartRequest.setBucketName(uploadParam.getBucketName());
        uploadPartRequest.setKey(uploadParam.getKey());
        uploadPartRequest.setUploadId(uploadParam.getUploadId());

        uploadPartRequest.setFile(uploadParam.getFile());
//        uploadPartRequest.setInputStream(uploadParam.getFile());
        // 设置当前块的长度
        uploadPartRequest.setPartSize(uploadParam.getSize());
        // 设置要上传的分块编号，从 1 开始
        uploadPartRequest.setPartNumber(uploadParam.getIndex());

        try {
            UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
            return uploadPartResult.getPartETag();

        } catch (CosClientException e) {
            throw e;
        }
    }

    private String completeMultipartUpload(List<PartETag> partETags, String key, String uploadId, String md5, String name) {
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(ossConfig.getBucketName(), key, uploadId, partETags);
        List<Serializable> range = redisTemplate.opsForList().range(name, 0, -1);

        try {

            CompleteMultipartUploadResult completeResult =
                    cosClient.completeMultipartUpload(completeMultipartUploadRequest);
            redisTemplate.delete(TAG_KEY + md5);


            return key;
        } catch (CosClientException e) {
            throw e;
        }
    }

    public boolean cancelSimpleUpload(String md5) {
        List<Serializable> range = redisTemplate.opsForList().range(TAG_KEY + md5, 0, 1);
        if (range == null) {
            return false;
        }
        String uploadId = (String) range.get(0);
        String fileKey = (String) range.get(1);
        AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(ossConfig.getBucketName(), fileKey, uploadId);
        try {
            cosClient.abortMultipartUpload(abortMultipartUploadRequest);
            redisTemplate.delete(TAG_KEY + md5);
            return true;
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFile(String key, String md5) {
        String formatKey = getFormatKey(key);
        cosClient.deleteObject(ossConfig.getBucketName(), formatKey);
        if (md5 != null) {
            redisTemplate.opsForHash().delete(MD5_KEY, md5);
        }
        return !isExist(formatKey);
    }

    @Override
    public boolean isExist(String key) {
        String formatKey = getFormatKey(key);
        return cosClient.doesObjectExist(ossConfig.getBucketName(), formatKey);
    }


    /**
     * 如果传递的是原本的路径（带前缀pic）加上bucketName
     * 如果传递的是带bucketName的路径，返回去除bucketName的路径
     *
     * @param key
     * @return
     */
    private String getFormatKey(String key) {
        String name = ossConfig.getBucketName() + "/";
        if (!key.contains(name)) {
            return key;
        }
        return key.replace("/" + name, "").replace(name, "").trim();
    }


    public String getMd5(String url) {
        String key = this.getFormatKey(url);
        String md5 = null;
        try {
            ObjectMetadata objectMetadata = cosClient.getObjectMetadata(ossConfig.getBucketName(), key);
            if (objectMetadata != null) {
                md5 = objectMetadata.getContentMD5();
            }
            return md5;
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String simpleUpload(MultipartFile file, String prev) {

        //获取客户端
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials
                (ossConfig.getSecretId(), ossConfig.getSecretKey());
        // 2 设置存储桶的地域（上文获得）
        Region region = new Region(ossConfig.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 使用https协议传输
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 4 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        if (prev != null && !prev.equals("")) {
            this.deleteFile(prev, null);
        }


        // 获取上传的文件的输入流
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 避免文件覆盖，获取文件的原始名称，如123.jpg,然后通过截取获得文件的后缀，也就是文件的类型
        String originalFilename = file.getOriginalFilename();
        // 获取文件的类型
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID工具  创建唯一名称，放置文件重名被覆盖，在拼接上上命令获取的文件类型
        String fileName = UUID.randomUUID() + fileType;
        // 指定文件上传到 COS 上的路径，即对象键。最终文件会传到存储桶名字中的images文件夹下的fileName名字
        String key = "picture/" + fileName;
        // 创建上传Object的Metadata
        ObjectMetadata objectMetadata = new ObjectMetadata();
        int available;
        try {
            available = inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // - 使用输入流存储，需要设置请求长度
        objectMetadata.setContentLength(available);
        // - 设置缓存
//        objectMetadata.setCacheControl("no-cache");
        objectMetadata.setHeader("Cache-Control", 60 * 60 * 3);
        // - 设置Content-Type
        objectMetadata.setContentType(fileType);
        //上传文件
        PutObjectResult putResult = cosClient.putObject
                (ossConfig.getBucketName(), key, inputStream, objectMetadata);
        // 创建文件的网络访问路径
        //关闭 cosClient，并释放 HTTP 连接的后台管理线程
        cosClient.shutdown();
        return getFormatKey(key);
    }


    public String part(InputStream inputStream) {
        //10613186

        return null;
    }

    private File transferToFile(MultipartFile multipartFile, String name, Integer index) throws IOException {
        String suffix = index + "-" + name.substring(name.lastIndexOf("."));
        File file = File.createTempFile(name, suffix);
        tempFile.put(name, file);
        multipartFile.transferTo(file);
        return file;
    }


    private void removeTemplate(String filenNme) {
        try {
            File file1 = tempFile.get(filenNme);
            tempFile.remove(filenNme);
            file1.delete();
        } catch (Exception e1) {

        }
    }
}
