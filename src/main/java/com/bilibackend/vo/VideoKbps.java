package com.bilibackend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 12:35
 * @Version 1.0
 */
@Data
public class VideoKbps implements Serializable {
    private Integer recv_30s;
    private Integer send_30s;
}