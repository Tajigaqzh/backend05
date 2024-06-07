package com.bilibackend.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.ObjectUtil;
import com.bilibackend.entity.Role;
import com.bilibackend.entity.User;
import com.bilibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description 权限认证和角色认证
 * @Date 2024/5/11 14:55
 * @Version 1.0
 */
@Component
public class StpInterfaceImpl implements StpInterface {


//    @Resource(name = "jsonRedisTemplate")
    @Qualifier("jsonRedisTemplate")
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //建议缓存起来
        return null;
    }

    /**
     * 先从redis获取，如果redis中没有，再从数据库查询，并保存到redis中
     *
     * @param loginId   userId
     * @param loginType 登录的类型
     * @return List
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String roles = (String) redisTemplate.opsForValue().get("login:users:" + loginId.toString());
        if (!StringUtils.isEmpty(roles)) {
            return Arrays.stream(roles.split(",")).toList();
        } else {
            User userInfo = userService.getUserInfo((Long) loginId);
            if (ObjectUtil.isNotNull(userInfo)) {
                List<String> role = userInfo.getRoles().stream().map(Role::getRoleTag).toList();
                String join = String.join(",", role);
                //保存3个小时
                redisTemplate.opsForValue().set("login:users:" + loginId, join, 3, TimeUnit.HOURS);
                return role;

            } else {
                return null;
            }
        }
    }
}
