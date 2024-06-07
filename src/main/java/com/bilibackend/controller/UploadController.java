package com.bilibackend.controller;

import com.bilibackend.dto.DeleteFileDto;
import com.bilibackend.service.OssService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/13 11:55
 * @Version 1.0
 */
@RestController

@Slf4j
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

    //http://pic.nwyzx.com/pic/ed8265e5-9eea-41d6-9d6b-d447d506d7dc.png

    /**
     * 第一个参数是上传的文件
     *
     * @param file 要上传的文件
     * @param prev 要删除的文件，可以为空
     * @return
     */
    @PostMapping("/simple")
    public Result simpleUpload(MultipartFile file, String prev) {
        String upload = ossService.simpleUpload(file, prev);
        return Result.success(upload);
    }


    @PostMapping("/transfer")
    public Result uploadTransfer(HttpServletRequest req) {
        String upload = ossService.transferManagerUpload(req);
        return Result.ok(upload);
    }

    /**
     * 前端分片
     *
     * @return
     */
    @PostMapping("/part")
    public Result uploadPart(HttpServletRequest request) {
//        System.out.println(uploadDto);

//        HashMap<String, Object> res = uploadService.uploadPart(uploadDto);
        String upload = ossService.partUpload(request);

//        System.out.println(upload);
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
