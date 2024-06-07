package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/15 13:06
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDeleteDto {
    @NotNull
    private List<Long> ids;
    private String publishName;
    private Long videoId;
}
