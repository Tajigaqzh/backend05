package com.bilibackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 14:51
 * @Version 1.0
 */
@Data
public class CoinDto {
    @NotNull
    private Long putId;

    @NotNull
    private Long videoId;

    @Max(3)
    @Min(1)
    private Long coin;
}
