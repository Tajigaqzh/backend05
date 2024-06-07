package com.bilibackend.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.document.VideoDocument;
import com.bilibackend.entity.History;
import com.bilibackend.entity.User;
import com.bilibackend.entity.Video;
import com.bilibackend.repository.VideoRepository;
import com.bilibackend.service.HistoryService;
import com.bilibackend.service.UserService;
import com.bilibackend.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/19 2:35
 * @Version 1.0
 */

public class PreferTask {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoRepository videoRepository;


    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    /**
     * 一天刷新一次
     */
    @Scheduled(cron = "0 10 1 * * ?")
    public void addUserPreferToRedis() {
        List<User> users = userService.list();
        //拿到所有user的id
        QueryWrapper<History> queryWrapper = new QueryWrapper<>();
        long time = DateUtil.offsetDay(new Date(), -1).getTime();
        queryWrapper.ge("recordTime", time);
        List<History> allHistory = historyService.list(queryWrapper);

        if (CollectionUtil.isEmpty(allHistory)) return;
        if (CollectionUtil.isEmpty(users)) return;

        List<Long> ids = users.stream().map(User::getId).toList();
//        HashMap<Long, List<Video>> userVideoHashMap = new HashMap<>();


        ids.forEach(id -> {
            List<History> histories = allHistory.stream().filter(history -> Objects.equals(history.getUid(), id)).toList();
            if (CollectionUtil.isNotEmpty(histories)) {
                List<String> list = histories.stream().map(History::getAuthorName).toList();
                //把最近观看的历史记录的作者名字用,拼接
                String concatNames = list.stream().reduce("", (s, name) -> s + name + ",");

                List<VideoDocument> byAuthorName = videoRepository.findByAuthorName(concatNames);
                if (CollectionUtil.isNotEmpty(byAuthorName)) {
                    List<Video> videos = byAuthorName.stream().map(ConvertUtil::convertToVideo).toList();
//                    userVideoHashMap.put(id, videos);
                    redisTemplate.opsForList().rightPushAll("history:recommend:" + id, videos.toArray());
                    //设置过期时间
                    redisTemplate.expire("history:recommend:" + id, 2, TimeUnit.DAYS);
                }
            }
        });


    }
}
