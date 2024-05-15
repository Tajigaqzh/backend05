package com.bilibackend.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.Role;
import com.bilibackend.entity.User;
import com.bilibackend.mapper.UserMapper;
import com.bilibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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
    protected UserMapper userMapper;


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

}
