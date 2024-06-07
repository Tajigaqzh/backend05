package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 16:07
 * @Version 1.0
 */
@Data
public class ZanDto {
    @NotNull
    private Long videoId;
    @NotNull
    private Boolean zan;
}
