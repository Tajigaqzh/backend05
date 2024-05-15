package com.bilibackend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 12:36
 * @Version 1.0
 */
@Data
public class Publish implements Serializable {
    private boolean active;
    private String cid;
}
