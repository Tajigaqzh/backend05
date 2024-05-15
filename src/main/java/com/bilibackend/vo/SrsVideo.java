package com.bilibackend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 12:38
 * @Version 1.0
 */

@Data
public class SrsVideo implements Serializable {
    private String codec;
    private String profile;
    private String level;
    private Integer width;
    private Integer height;

}
