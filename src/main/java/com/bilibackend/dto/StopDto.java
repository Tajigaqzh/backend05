package com.bilibackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 13:25
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopDto {
    @NotBlank
    private String roomId;
}
