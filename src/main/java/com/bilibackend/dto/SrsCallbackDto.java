package com.bilibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/13 16:33
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrsCallbackDto {
    private String server_id;
    private String service_id;
    private String action;
    private String client_id;
    private String ip;
    private String vhost;
    private String app;
    private String stream;
    private String tcUrl;
    private String param;
    private String pageUrl;
    private String stream_url;
    private String stream_id;
}
