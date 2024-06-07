package com.bilibackend.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.History;
import com.bilibackend.entity.Video;
import com.bilibackend.mapper.HistoryMapper;
import com.bilibackend.mapper.VideoMapper;
import com.bilibackend.service.HistoryService;
import com.bilibackend.vo.PageResultVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 20126
 * @description 针对表【bili_history】的数据库操作Service实现
 * @createDate 2024-05-16 01:49:03
 */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;


    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 锁，锁定更新数据表中的playTimes
     */
    private static String playTimeLockPrefix = "lock:playTime:";


    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    /**
     * data,某个视频的正在观看人数，过期时间3小时，如果有则继续新增
     * 请求视频详情的时候新增，用户点击返回或者退出，或者完成播放的时候减少
     */
    private static String detailCount = "detail:count:";


    /**
     * 锁，锁定视频的正在观看次数
     */
    private static String detailLockPrefix = "detail:lock:";


    @Override
    public PageResultVo historyPage(Long page, Long size, Long uid) {

        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();
        historyQueryWrapper.eq("uid", uid);
        historyQueryWrapper.orderByDesc("recordTime");
        IPage<History> historyIPage = historyMapper.listPage(new Page<>(page, size), historyQueryWrapper);
        return PageResultVo.builder()
                .current(historyIPage.getCurrent())
                .total(historyIPage.getTotal())
                .data(historyIPage.getRecords())
                .build();
    }

    @Override
    public List<History> searchCondition(History history) {
        String title = history.getTitle();
        Long uid = history.getUid();
        String authorName = history.getAuthorName();
        Date recordTime = history.getRecordTime();
        String isMobile = history.getIsMobile();
        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();

        if (history.getTitle() != null) {
            historyQueryWrapper.eq("title", title).eq("uid", uid);

        } else if (authorName != null) {
            historyQueryWrapper.eq("authorName", authorName).eq("uid", uid);
        } else if (recordTime != null) {
            historyQueryWrapper.ge("recordTime", recordTime).eq("uid", uid);
        } else if (isMobile != null) {
            historyQueryWrapper.eq("isMobile", isMobile).eq("uid", uid);
        }

        historyQueryWrapper.orderByDesc("recordTime");
        return historyMapper.selectList(historyQueryWrapper);
    }

    @Override
    public boolean addHistory(History history) {


        Long watched = history.getWatched();
        boolean updatePlay = true;
        if (watched > 0) {
            updatePlay = this.updatePlayTimes(history.getVideoId());
        }


        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();
        historyQueryWrapper.eq("uid", history.getUid());
        historyQueryWrapper.eq("videoId", history.getVideoId());
        History history1 = historyMapper.selectOne(historyQueryWrapper);
        boolean result;
        if (history1 != null) {
            result = this.update(history, historyQueryWrapper);
        } else {
            result = this.save(history);
        }

        return (updatePlay && result);
    }

    @Override
    public boolean updateHistory(History history) {

        QueryWrapper<History> historyQueryWrapper = new QueryWrapper<>();
        historyQueryWrapper.eq("uid", history.getUid());
        historyQueryWrapper.eq("videoId", history.getVideoId());


        Long videoId = history.getVideoId();

        RLock lock = redissonClient.getLock(detailLockPrefix + videoId);
        Long count;
        lock.lock();
        try {
            //正在观看人数，todo 离开的时候减少
            count = redisTemplate.opsForValue().decrement(detailCount + videoId);

            if (count != null && count <= 0) {
                redisTemplate.expire(detailCount + videoId, 1, TimeUnit.SECONDS);
            }
        } finally {
            lock.unlock();
        }

        int update1 = historyMapper.update(history, historyQueryWrapper);
        return update1 > 0;
    }

    private boolean updatePlayTimes(Long videoId) {
        RLock lock = redissonClient.getLock(playTimeLockPrefix + videoId);
        lock.lock();

        try {
            Video video = videoMapper.selectById(videoId);
            if (ObjectUtil.isNotNull(video)) {
                int i = videoMapper.updateById(Video.builder().id(videoId).playTimes(video.getPlayTimes() + 1).build());
                return i > 0;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }
}




