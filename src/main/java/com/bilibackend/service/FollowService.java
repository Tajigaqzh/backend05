package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.Follow;
import com.bilibackend.entity.User;

import java.util.List;

/**
 * @author 20126
 * @description 针对表【bili_follow】的数据库操作Service
 * @createDate 2024-05-16 13:25:44
 */
public interface FollowService extends IService<Follow> {
    List<User> getListByUpId(Long upId);

    List<User> getListByUserId(Long uid);
}
