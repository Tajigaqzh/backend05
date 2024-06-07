package com.bilibackend.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.bilibackend.dto.VideoDto;
import com.bilibackend.dto.VideoUpdateDto;
import com.bilibackend.dto.ZanDto;
import com.bilibackend.entity.Video;
import com.bilibackend.entity.VideoType;
import com.bilibackend.service.VideoService;
import com.bilibackend.service.VideoTypeService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.vo.PageResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @Autowired
    private VideoTypeService videoTypeService;


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
    public Result getVideoDetail(@RequestParam("id") Long id) {
        System.out.println(id);
        Video detailById = videoService.getDetailById(id);
        return Result.success(detailById);
    }


    @GetMapping("/type")
    public Result types() {
        List<VideoType> list = videoTypeService.list();
        return Result.success(list);
    }

    //todo 增删改查
    @PostMapping("/add")
    public Result add(@RequestBody VideoDto videoDto) {
        boolean save = videoService.saveVideo(videoDto);
        //todo addToES

        if (save) {
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);
    }

    /**
     * 将用户未登录状态下播放的视频放到redis缓存中，避免首页刷新的时候重复推荐
     *
     * @param ids 视频ids
     * @return
     */
    @PostMapping("/cache")
    public Result addLocalHistoryToCache(@RequestBody List<Long> ids) {
        videoService.addToCache(ids);
        return Result.ok();
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

    @PostMapping("/zan")
    public Result zan(@RequestBody @Validated ZanDto zanDto) {
        boolean zan = videoService.zan(zanDto.getVideoId(), zanDto.getZan());
        if (zan) return Result.ok();
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    @GetMapping("/search")
    public Result search(@RequestParam("keyword") String keyword, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        List<Video> videos = videoService.searchPge(keyword, page, size);
        if (CollectionUtil.isNotEmpty(videos)) {
            return Result.ok(videos);
        }
        return Result.error(ResultCode.QUERY_ERROR);
    }

}
