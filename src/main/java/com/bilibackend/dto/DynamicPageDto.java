package com.bilibackend.dto;

import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/17 2:32
 * @Version 1.0
 */
@Data
public class DynamicPageDto {
    private Long page;
    private Long size;
    private Long userId;
}
