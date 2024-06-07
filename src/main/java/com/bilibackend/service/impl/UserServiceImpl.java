package com.bilibackend.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.dto.CoinDto;
import com.bilibackend.entity.Role;
import com.bilibackend.entity.User;
import com.bilibackend.entity.Video;
import com.bilibackend.mapper.UserMapper;
import com.bilibackend.mapper.VideoMapper;
import com.bilibackend.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 15:29
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedissonClient redissonClient;


    private static final String coinLockPrefix = "coin:";

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    @Override
    public User loginByUserName(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User byUsername = userMapper.selectOne(wrapper);
        if (byUsername == null) return null;
        String queryPassword = byUsername.getPassword();
        if (BCrypt.checkpw(password, queryPassword)) {
            return byUsername;
        }
        return null;
    }

    @Override
    public User loginByMobile(String mobile, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        User byMobile = userMapper.selectOne(wrapper);
        if (byMobile == null) return null;
        String queryPassword = byMobile.getPassword();
        if (BCrypt.checkpw(password, queryPassword)) {
            return byMobile;
        }
        return null;
    }

    @Override
    public User getUserInfo(Long uid) {
        User userInfo = userMapper.getUserInfo(uid);

        if (ObjectUtil.isNotNull(userInfo)) {
            String join = String.join(",", userInfo.getRoles().stream().map(Role::getRoleTag).toList());
            //保存3个小时
            redisTemplate.opsForValue().set("login:users:" + uid.toString(), join, 3, TimeUnit.HOURS);
            userInfo.setPassword(null);
        }
        return userInfo;
    }

    @Override
    @Transactional
    public boolean putCoin(CoinDto coinDto) {
        RLock videoCoinLock = null;
        RLock putLock = null;
        RLock receiveLock = null;
        try {
            Long coin = coinDto.getCoin();
            Long videoId = coinDto.getVideoId();
            Long putId = coinDto.getPutId();

            //从这里开始锁，3把锁  user1，user2，video
            videoCoinLock = redissonClient.getLock(coinLockPrefix + "video:" + videoId);

            videoCoinLock.lock();
            Video video = videoMapper.getDetail(videoId);

            if (ObjectUtil.isNull(video)) return false;

            //此视频已有的coin
            Long videoCoin = video.getCoin();

            //此视频的作者们
            List<User> publisher = video.getPublisher();

            if (ObjectUtil.isNull(publisher)) return false;

            //此视频的第一作者
            User user = publisher.get(0);

            if (ObjectUtil.isNull(user)) return false;
            Long receiveId = user.getId();


            //更新video的coin
            Video build = Video.builder().id(videoId).coin(videoCoin + coin).build();

            List<Long> queryIds = new ArrayList<>();
            queryIds.add(putId);
            queryIds.add(receiveId);

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("id", queryIds);

            putLock = redissonClient.getLock(coinLockPrefix + "user:" + putId);

            receiveLock = redissonClient.getLock(coinLockPrefix + "user:" + receiveId);

            putLock.lock();
            receiveLock.lock();

            //获取之前锁上
            List<User> users = this.list(userQueryWrapper);
            if (users.size() != 2) return false;

            User putUser = users.stream().filter(user1 -> Objects.equals(user1.getId(), putId)).toList().get(0);
            User receiveUser = users.stream().filter(user1 -> Objects.equals(user1.getId(), receiveId)).toList().get(0);

            ArrayList<User> updateUsers = new ArrayList<>();

            User putUpdate = User.builder().id(putId).coin(putUser.getCoin() - coin).build();
            User receiveUpdate = User.builder().id(receiveId).coin(receiveId).coin(receiveUser.getCoin() + coin).build();

            updateUsers.add(receiveUpdate);
            updateUsers.add(putUpdate);

            //更新两个表，开启事务
            videoMapper.updateById(build);
            this.updateBatchById(updateUsers);
            return true;

        } finally {
            if (videoCoinLock != null) {
                videoCoinLock.unlock();
            }
            if (putLock != null) {
                putLock.unlock();
            }
            if (receiveLock != null) {
                receiveLock.unlock();
            }
        }
    }

}
