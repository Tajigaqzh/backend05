package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/15 17:02
 * @Version 1.0
 */

@Data
public class AgreeDto {
    @NotNull
    private Long id;
    private Boolean agree;
    private Boolean disagree;
}
