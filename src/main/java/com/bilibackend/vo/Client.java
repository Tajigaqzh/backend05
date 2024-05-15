package com.bilibackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 10:44
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {
    private String id;

    private String vhost;

    private String stream;
    private String ip;
    private String pageUrl;
    private String swfUrl;
    private String tcUrl;
    private String url;
    private String name;
    private String type;
    private Boolean publish;
    private Integer alive;
    private Integer send_bytes;
    private Integer recv_bytes;
    private VideoKbps kbps;

}
