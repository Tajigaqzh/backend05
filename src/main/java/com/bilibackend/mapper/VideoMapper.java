package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibackend.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 16:40
 * @Version 1.0
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    IPage<Video> listPage(Page<Video> page, @Param(Constants.WRAPPER) Wrapper<Video> ew);


    /**
     * 查询video的详情以及评论
     *
     * @param videoId videoId
     * @return
     */
    Video getDetail(@Param("videoId") Long videoId);
}
