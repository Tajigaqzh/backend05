package com.bilibackend.controller;

import com.bilibackend.dto.DeleteFileDto;
import com.bilibackend.service.OssService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/transfer")
    public Result uploadTransfer(HttpServletRequest req) {
        String upload = ossService.transferManagerUpload(req);
        return Result.ok(upload);
    }

    /**
     * 前端分片
     *
     * @param req
     * @return
     */
    @PostMapping("/part")
    public Result uploadPart(HttpServletRequest req) {
        String upload = ossService.partUpload(req);
        return Result.ok(upload);
    }

    /**
     * @param md5
     * @return 取消上传
     */
    @GetMapping("/cancel")
    public Result cancelUpload(@Validated @NotNull String md5) {
        ossService.cancelSimpleUpload(md5);
        return Result.ok();
    }

    /**
     * 不建议直接使用，删除图片的时候可以使用，删除视频的时候要传md5
     *
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@RequestBody @Validated DeleteFileDto deleteFileDto) {
        boolean b = ossService.deleteFile(deleteFileDto.getKey(), deleteFileDto.getMd5());
        if (b) {
            return Result.ok();
        }
        return Result.error(ResultCode.DELETE_ERROR);
    }

    @GetMapping("/md5")
    public Result getMD5(String url) {
        return Result.success(ossService.getMd5(url));
    }
}
