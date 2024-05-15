package com.bilibackend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 11:03
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResultVO implements Serializable {
    private Integer code;
    private LoginData data;
    private Integer server;


    @Data
    public class LoginData implements Serializable {
        private String token;

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
        private Date createAt;

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
//        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        private Date expireAt;
    }
}
