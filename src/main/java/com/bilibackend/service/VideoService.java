package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.dto.VideoDto;
import com.bilibackend.dto.VideoUpdateDto;
import com.bilibackend.entity.Video;
import com.bilibackend.vo.PageResultVo;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 14:57
 * @Version 1.0
 */

public interface VideoService extends IService<Video> {

    PageResultVo recommendPage(Long page, Long size);


    PageResultVo recommendTypePage(Long page, Long size, String type);

    /**
     * @param id videoId 如果处于登录状态，会将视频id添加到redis缓存中
     * @return 
     */
    Video getDetailById(Long id);

    
    boolean saveVideo(VideoDto videoDto);

    /**
     * 有些视频是离线观看的，无法加入历史记录，如果观看后，前端缓存这些id的话，再次登录的时候也可以加入redis缓存
     * @param ids 这个接口要求必须登录才能访问
     */
    void addToCache(List<Long> ids);

    boolean removeVideo(Long id);


    boolean updateVideo(VideoUpdateDto videoUpdateDto);

    boolean zan(Long videoId,boolean isAdd);

    boolean updatePlayTimes(Long id);

    List<Video> searchPge(String keyword, Integer page, Integer size);
}
