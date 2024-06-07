package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.dto.CoinDto;
import com.bilibackend.entity.User;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 15:29
 * @Version 1.0
 */
public interface UserService extends IService<User> {
    User loginByUserName(String username, String password);

    User loginByMobile(String mobile, String password);

    User getUserInfo(Long uid);

    boolean putCoin(CoinDto coinDto);
}
