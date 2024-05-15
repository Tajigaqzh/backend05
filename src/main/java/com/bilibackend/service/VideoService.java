package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.dto.VideoDto;
import com.bilibackend.dto.VideoUpdateDto;
import com.bilibackend.entity.Video;
import com.bilibackend.vo.PageResultVo;

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
     * @param id videoId
     * @return
     */
    Video getDetailById(Long id);

    boolean saveVideo(VideoDto videoDto);


    boolean removeVideo(Long id);


    boolean updateVideo(VideoUpdateDto videoUpdateDto);
}
