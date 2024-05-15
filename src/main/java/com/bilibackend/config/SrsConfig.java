package com.bilibackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/10 20:22
 * @Version 1.0
 */
@Configuration
public class SrsConfig {

    @Value("${srs.server}")
    private String server;
    @Value("${srs.serverHost}")
    private String serverHost;


    @Value("${srs.publishSecret}")
    private String publishSecret;

    @Value("${srs.publishUri}")
    private String publishUri;

    @Value("${srs.srsLogin}")
    private String srsLogin;

    @Value("${srs.loginPassword}")
    private String loginPassword;

    @Value("${srs.streamsUri}")
    private String streamsUri;


    @Value("${srs.clientsUri}")
    private String clientsUri;

    @Value("${srs.playPrefix}")
    private String playPrefix;




    /**
     * <a href="http://150.158.122.237/rtc/v1/whip/?app=live">...</a>
     * <a href="https://live.nwyzx.com/rtc/v1/whip/?app=live"
     * <p>
     * &stream=${videoName}&secret=078725fe5e1b436e95a14fae8ec518cf
     * 发布直播接口
     *
     * @return url
     */
    public String getFullPublishURL(String roomId) {
        if (this.serverHost != null) {
            return this.serverHost + this.publishUri + "&stream=" + roomId + "&secret=" + publishSecret;
        }
        return this.server + this.publishUri + "&stream=" + roomId + "&secret=" + publishSecret;
    }


    /**
     * <a href="http://150.158.122.237/terraform/v1/mgmt/login">...</a>
     * 登录接口
     */
    public String getFullLoginURL() {
        if (this.serverHost != null) {
            return this.serverHost + this.srsLogin;
        }

        return this.server + this.srsLogin;
    }

    public String getPassword() {
        return this.loginPassword;
    }

    /**
     * <a href="http://150.158.122.237/api/v1/streams">...</a>
     *
     * @return url 分页获取streams的接口，需要token
     */
    public String getFullStreamsURL() {
        if (this.serverHost != null) {
            return this.serverHost + this.streamsUri;
        }
        return this.server + this.streamsUri;
    }


    /**
     * 获取在线客户信息接口，get查询，delete关闭
     * delete /api/v1/clients/{publishId}
     * @return url
     */
    public String getClientsURL() {
        if (this.serverHost != null) {
            return this.serverHost + this.clientsUri;
        }
        return this.server + this.clientsUri;
    }

    public String getPlayPrefix(){
        return this.playPrefix;
    }
}
