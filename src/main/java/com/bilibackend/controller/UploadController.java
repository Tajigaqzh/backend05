package com.bilibackend.controller;

import com.bilibackend.service.OssService;
import com.bilibackend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/13 11:55
 * @Version 1.0
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private OssService ossService;



    /**
     * 前端检查data，如果data有url，就不用上传，如果没有，还要上传
     *
     * @param md5 md5
     * @return Result
     */
    @GetMapping(value = "/check")
    public Result checkFileExists(@Validated @NotNull String md5) {
        return Result.success(ossService.uploadCheck(md5));
    }


    public Result upload(HttpServletRequest req) {
        return Result.ok();

    }
}
