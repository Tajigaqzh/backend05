package com.bilibackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/10 16:24
 * @Version 1.0
 */
@RestController
@RequestMapping("/tencent/callback")
public class OssCallback {


    @PostMapping("/ban")
    public String banVideo() {
        return null;
    }

}
