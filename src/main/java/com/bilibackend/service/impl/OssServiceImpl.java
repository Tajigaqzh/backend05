package com.bilibackend.service.impl;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.bilibackend.config.OssConfig;
import com.bilibackend.service.OssService;
import com.bilibackend.utils.UploadParam;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
//        objectMetadata.addUserMetadata("md5", md5);
        request.setObjectMetadata(objectMetadata);

        try {
            InitiateMultipartUploadResult initResult = cosClient.initiateMultipartUpload(request);
            // 获取 uploadId
            return initResult.getUploadId();
        } catch (CosClientException e) {
            throw e;
        }

    }

    @Override
    public String partUpload(HttpServletRequest req) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
        //文件分片数据
        MultipartFile file = multipartRequest.getFile("file");
        if (ObjectUtil.isNull(file)) {
            return null;
        }
        int index = Integer.parseInt(multipartRequest.getParameter("index"));
        String md5 = multipartRequest.getParameter("md5");
        // 总片数
        int total = Integer.parseInt(multipartRequest.getParameter("total"));

        InputStream inputStream;
        Long size;
        try {
            size = file.getSize();
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (ObjectUtil.isNull(inputStream)) {
            return null;
        }

        //第一次上传
        if (index == 1) {
            String name = IdUtil.randomUUID();
            String fileType = FileTypeUtil.getType(inputStream);

            String fileKey;
            if (fileType.contains("mp4")) {
                fileKey = "video/" + name + "." + fileType;
            } else {
                fileKey = "pic/" + name + "." + fileType;
            }
            //将uploadId缓存起来，后面继续放入tags，用于合并的时候

            String uploadId = this.initMultipartUpload(ossConfig.getBucketName(), fileKey, md5);
            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .inputStream(inputStream)
                    .index(index)
                    .key(fileKey).build();

            PartETag partETag = this.uploadPart(build);
            // 第一个放的是uploadId,第二个是文件名
            redisTemplate.opsForList().rightPushAll(TAG_KEY + md5, uploadId, fileKey, partETag);
            return fileKey;
        } else if (index < total) {
            //中间的
            List<Serializable> range = redisTemplate.opsForList().range(TAG_KEY + md5, 0, 1);
            if (range == null) {
                return null;
            }
            String uploadId = (String) range.get(0);
            String fileKey = (String) range.get(1);
            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .inputStream(inputStream)
                    .index(index)
                    .key(fileKey).build();
            PartETag partETag = this.uploadPart(build);
            redisTemplate.opsForList().rightPush(TAG_KEY + md5, partETag);

            return fileKey;
        } else {
            List<Serializable> range = redisTemplate.opsForList().leftPop(TAG_KEY + md5, total + 1);

            if (range == null) {
                return null;
            }
            String uploadId = (String) range.get(0);
            String fileKey = (String) range.get(1);
            range.remove(0);
            range.remove(1);
            List<PartETag> partETags = new ArrayList<>();
            range.forEach(item -> {
                partETags.add((PartETag) item);

            });
            UploadParam build = UploadParam.builder()
                    .bucketName(ossConfig.getBucketName())
                    .uploadId(uploadId)
                    .size(size)
                    .inputStream(inputStream)
                    .index(index)
                    .key(fileKey).build();
            PartETag partETag = this.uploadPart(build);
            partETags.add(partETag);
            String b = this.completeMultipartUpload(partETags, fileKey, uploadId, md5);
            if (ObjectUtil.isNotNull(b)) {
                String url = getFormatKey(fileKey);
                redisTemplate.opsForHash().put(MD5_KEY, md5, url);
                return url;
            }
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

        uploadPartRequest.setInputStream(uploadParam.getInputStream());
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

    private String completeMultipartUpload(List<PartETag> partETags, String key, String uploadId, String md5) {
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(ossConfig.getBucketName(), key, uploadId, partETags);

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
        return key.replace(name, "").trim();
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
}
