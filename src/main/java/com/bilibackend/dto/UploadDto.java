package com.bilibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/21 18:48
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDto {
    private MultipartFile file;
    private Integer index;
    private Integer total;
    private String md5;
}
