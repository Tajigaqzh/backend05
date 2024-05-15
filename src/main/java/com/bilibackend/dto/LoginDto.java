package com.bilibackend.dto;

import com.bilibackend.validate.AllowValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 15:23
 * @Version 1.0
 */
@Data
public class LoginDto {

    private String username;

    //暂时先设置为不为空，后面可能新增其他登录方式，就不用password了
    @NotBlank
    private String password;

    private String mobile;

    @AllowValue(intValues = {0, 1, 2, 3})
    private int type;
}
