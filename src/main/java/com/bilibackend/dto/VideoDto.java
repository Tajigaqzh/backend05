package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/15 19:14
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoDto {
    @NotNull
    private String title;
    @NotNull
    private List<Long> ids;

    private Long groupId;
    @NotNull
    private String keyWord;
    @NotNull
    private String typeName;

    @NotNull
    private String description;
    @NotNull
    private String cover;
    @NotNull
    private Long length;
    @NotNull
    private String url;
}
