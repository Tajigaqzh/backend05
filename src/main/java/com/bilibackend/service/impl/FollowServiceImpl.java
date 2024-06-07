package com.bilibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.Follow;
import com.bilibackend.entity.User;
import com.bilibackend.mapper.FollowMapper;
import com.bilibackend.mapper.UserMapper;
import com.bilibackend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 20126
 * @description 针对表【bili_follow】的数据库操作Service实现
 * @createDate 2024-05-16 13:25:44
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getListByUpId(Long upId) {
        return userMapper.getFollowMe(upId);

    }

    @Override
    public List<User> getListByUserId(Long uid) {
        return userMapper.getMyFollow(uid);
    }
}




