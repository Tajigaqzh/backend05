package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 13:22
 * @Version 1.0
 */
@Data
public class FollowDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long upId;
    @NotNull
    private Boolean isFollow;
}
