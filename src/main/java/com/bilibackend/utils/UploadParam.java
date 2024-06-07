package com.bilibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.InputStream;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 0:40
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadParam {
    private File file;
    private Long size;
    private String key;
    private String uploadId;
    private String bucketName;
    private Integer index;
}
