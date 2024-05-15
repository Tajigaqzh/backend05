package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/15 22:12
 * @Version 1.0
 */
@Data
public class VideoUpdateDto {
    @NotNull
    private Long id;

    private String title;

    private Long groupId;

    private String keyWord;

    private String typeName;

    private String description;
}
