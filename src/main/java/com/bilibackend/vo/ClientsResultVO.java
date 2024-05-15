package com.bilibackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 10:35
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientsResultVO implements Serializable {
    private Integer code;
    private String server;
    private String service;
    private Integer pid;
    private List<Client> clients;

}
