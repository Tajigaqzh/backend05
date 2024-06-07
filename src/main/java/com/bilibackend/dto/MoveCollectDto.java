package com.bilibackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 16:47
 * @Version 1.0
 */
@Data
public class MoveCollectDto {
    @NotNull
    private Long fromId;
    @NotNull
    private Long toId;
    @NotNull
    private List<Long> ids;
}
