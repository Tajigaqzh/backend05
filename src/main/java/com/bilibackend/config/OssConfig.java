package com.bilibackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 12:17
 * @Version 1.0
 */
@Configuration
public class OssConfig {

    @Value("${oss.secretId}")
    private String secretId;

    @Value("${oss.secretKey}")
    private String secretKey;

    @Value("${oss.bucketName}")
    private String bucketName;


    @Value("${oss.region}")
    private String region;

    public String getRegion() {
        return this.region;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getSecretId() {
        return this.secretId;
    }


    public String getBucketName() {
        return bucketName;
    }


    @Bean
    public COSClient cosClient() {
        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    @Bean
    public TransferManager transferManager(COSClient cosClient) {
        //自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可
        ExecutorService threadPool = Executors.newFixedThreadPool(16);

        // 传入一个 threadPool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        //设置何时使用分段上传的大小阈值（以字节为单位）。超过此大小的上传将自动使用分段上传策略，而小于此阈值的上传将使用单个连接上传整个对象。
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        //分块大小
        transferManagerConfiguration.setMinimumUploadPartSize(1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }


}
