package com.bilibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.UserRole;
import com.bilibackend.mapper.UserRoleMapper;
import com.bilibackend.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 10:18
 * @Version 1.0
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
