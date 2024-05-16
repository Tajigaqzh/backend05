package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 1:58
 * @Version 1.0
 */
@Data
public class DeleteFileDto {
    @NotNull
    private String key;
    private String md5;
}
