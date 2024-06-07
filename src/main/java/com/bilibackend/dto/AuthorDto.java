package com.bilibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/22 16:30
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private Long id;
    private String name;
}
