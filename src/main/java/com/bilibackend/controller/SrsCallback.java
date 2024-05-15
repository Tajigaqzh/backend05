package com.bilibackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bilibackend.dto.SrsCallbackDto;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.entity.Watcher;
import com.bilibackend.service.LiveRoomService;
import com.bilibackend.service.WatcherService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/10 18:31
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/callback")
public class SrsCallback {
    @Autowired
    private LiveRoomService liveRoomService;


    @Autowired
    private WatcherService watcherService;


    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 锁，锁定视频的正在观看次数
     */
    private static String liveCountLockPrefix = "live:lock:watching:";

    /**
     * data,某个视频的正在观看人数，过期时间3小时，如果有则继续新增
     * 请求视频详情的时候新增，用户点击返回或者退出，或者完成播放的时候减少
     */
    private static String liveCount = "live:count:";

    /**
     * 加入房间的回调
     * todo 通知在线用户，某人加入了房间，这个通信用socket发吧，在这里仅做鉴权
     * 在线观看人数要查询
     * @return
     */
    /**
     * @param requestBody
     * @return
     */
    @PostMapping("/play")
    public Result enterRoomCallback(@RequestBody SrsCallbackDto requestBody) {
        System.out.println(requestBody);
        String param = requestBody.getParam();
        String token = null;
        String roomId = null;
        if (param.startsWith("?") && param.contains("&")) {
            String param2 = param.replace("?", "").trim();
            List<String> list = Arrays.stream(param2.split("&")).toList();
            for (String s : list) {
                if (s.startsWith("roomId")) {
                    roomId = s.split("=")[1];
                } else if (s.startsWith("token")) {
                    token = s.split("=")[1];
                }
            }
            if (token == null) {
                return Result.error(ResultCode.ERROR);
            }
            Object loginId = StpUtil.getLoginIdByToken(token);

            if (loginId != null) {


                LiveRoom byId = liveRoomService.getById(roomId);

                if (ObjectUtil.isNotNull(byId) && byId.getEndTime() == null) {
                    Watcher build = Watcher.builder().ip(requestBody.getIp())
                            .enterTime(new Date())
                            .client_id(requestBody.getClient_id())
                            .service_id(requestBody.getService_id())
                            .stream(requestBody.getStream())
                            .userId(Long.parseLong(loginId.toString())).build();
                    boolean save = watcherService.save(build);
                    //todo 退出的时候减少
                    RLock lock = redissonClient.getLock(liveCountLockPrefix + roomId);
                    lock.lock();

                    try {
                        redisTemplate.opsForValue().increment(liveCount + roomId);
                        redisTemplate.expire(liveCount + roomId, 3, TimeUnit.HOURS);
                    } finally {
                        lock.unlock();
                    }


                    if (save) {
                        return Result.result(ResultCode.SUCCESS_CALLBACK, "ok");
                    }
                }
            }

        }
        return Result.result(ResultCode.ERROR_CALLBACK, "ok");
    }


    /**
     * 主播下线
     *
     * @return
     */
    @PostMapping("/unPublish")
    public Result unPublish() {
        //要通知其他人主播下线了
        return Result.result(ResultCode.SUCCESS_CALLBACK, "ok");
    }

    /**
     * 这个回调暂时不用写
     *
     * @param request
     * @return
     */
    @PostMapping("/publish")
    public Result publishCallback(HttpServletRequest request) {
        return Result.result(ResultCode.SUCCESS_CALLBACK, "ok");
    }


}
