package com.bilibackend.controller;

import com.bilibackend.dto.VideoDto;
import com.bilibackend.dto.VideoUpdateDto;
import com.bilibackend.entity.Video;
import com.bilibackend.service.VideoService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.vo.PageResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 15:52
 * @Version 1.0
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;


    @GetMapping("/pageType")
    public Result recommendType(@RequestParam("page") Long page, @RequestParam("size") Long size,
                                @RequestParam("type") String type) {
        PageResultVo pageResultVo = videoService.recommendTypePage(page, size, type);
        return Result.success(pageResultVo);
    }

    /**
     * 首页推荐视频，少一个coverImage
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/pageSimple")
    public Result recommendPage(@RequestParam("page") Long page, @RequestParam("size") Long size) {
        PageResultVo pageResultVo = videoService.recommendPage(page, size);
        return Result.success(pageResultVo);
    }

    /**
     * 点击视频的时候更新在线观看人数
     * 获取评论，详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Result getVideoDetail(Long id) {
        Video detailById = videoService.getDetailById(id);
        return Result.success(detailById);
    }

    //todo 增删改查
    @PostMapping("/add")
    public Result add(@RequestBody VideoDto videoDto) {
        boolean save = videoService.saveVideo(videoDto);
        if (save) {
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);
    }

    @PostMapping("/update")
    public Result update(@RequestBody VideoUpdateDto videoUpdateDto) {
        boolean b = videoService.updateVideo(videoUpdateDto);
        if (b) {
            return Result.ok();
        }
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam("id") Long id) {
        boolean delete = videoService.removeVideo(id);
        if (delete) return Result.ok();
        return Result.error(ResultCode.DELETE_ERROR);
    }
}
