package com.bilibackend.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.entity.Video;
import com.bilibackend.entity.VideoType;
import com.bilibackend.mapper.VideoMapper;
import com.bilibackend.mapper.VideoTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 19:57
 * @Version 1.0
 */
@Slf4j
@Component
public class RecommendTask {

    private static String recommendPrefix = "video:recommend";


    private static String recommendTypePrefix = "video:type:";

    @Autowired
    private VideoMapper videoMapper;


    /**
     * video的要改成id
     */
    @Autowired
    private VideoTypeMapper videoTypeMapper;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    /**
     *
     */
    @Scheduled(cron = "0 0 9,12,15,18,21 * * ?")
    public void updateRecommendTask() {
        //一周之前，以及15天之前到现在播放超过3000的
        long lastWeek = DateUtil.lastWeek().getTime();

        log.info("开始执行定时任务1");
        //从数据库分页查询
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        long lastTwoWeek = DateUtil.offsetDay(new Date(), -15).getTime();
        videoQueryWrapper
                .ge("createTime", lastWeek)
                .orderByAsc("createTime").or(videoQueryWrapper1 -> {
                    videoQueryWrapper1.ge("createTime", lastTwoWeek).and(videoQueryWrapper2 -> {
                        videoQueryWrapper2.ge("playTimes", 3000);
                    });
                });

        List<Video> videos = videoMapper.selectList(videoQueryWrapper);
        videos.forEach(video -> {
            video.setWatching(null);
            video.setCoin(null);
            video.setCollect(null);
            video.setPlayTimes(null);
            video.setTransmit(null);
            video.setCoin(null);
            video.setLikeNumber(null);
        });

        Object[] objects = videos.toArray();

        redisTemplate.delete(recommendPrefix);
        redisTemplate.boundListOps(recommendPrefix).rightPushAll(objects);
        log.info("完成定时任务1");
    }

    /**
     *
     */
    @Scheduled(cron = "20 0 3,9,15,21 * * ?")
//    @Scheduled()
    public void updateTypeRecommendTask() {
        List<VideoType> videoTypes = videoTypeMapper.selectList(new QueryWrapper<>());
        List<String> typeNames = videoTypes.stream().map(VideoType::getTypeName).toList();

        //一周之前，以及15天之前到现在播放超过3000的
        long lastWeek = DateUtil.lastWeek().getTime();

        long lastTwoWeek = DateUtil.offsetDay(new Date(), -15).getTime();

        typeNames.forEach(typeName -> {
            QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
            videoQueryWrapper
                    .ge("createTime", lastWeek).eq("typeName", typeName)
                    .orderByAsc("createTime").or(videoQueryWrapper1 -> {
                        videoQueryWrapper1.ge("createTime", lastTwoWeek).and(videoQueryWrapper2 -> {
                            videoQueryWrapper2.eq("typeName", typeName).ge("playTimes", 3000);
                        });
                    });

            List<Video> videos = videoMapper.selectList(videoQueryWrapper);

            videos.forEach(video -> {
                video.setWatching(null);
                video.setCoin(null);
                video.setCollect(null);
                video.setPlayTimes(null);
                video.setTransmit(null);
                video.setCoin(null);
                video.setLikeNumber(null);
            });

            Object[] objects = videos.toArray();

            redisTemplate.delete(recommendTypePrefix + typeName);
            redisTemplate.boundListOps(recommendPrefix + typeName).rightPushAll(objects);
            log.info("完成定时任务2--" + typeName + "数据的更新");
        });

        log.info("完成定时任务2--数据的更新");
    }
}
