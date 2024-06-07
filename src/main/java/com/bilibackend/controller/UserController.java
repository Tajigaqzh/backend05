package com.bilibackend.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.dto.CoinDto;
import com.bilibackend.dto.LoginDto;
import com.bilibackend.entity.CollectionFolder;
import com.bilibackend.entity.User;
import com.bilibackend.entity.UserRole;
import com.bilibackend.service.CollectionFolderService;
import com.bilibackend.service.UserRoleService;
import com.bilibackend.service.UserService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.validate.AddGroup;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @Author 20126
 * @Description 投币涉及用户，写到了user中
 * @Date 2024/5/11 13:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;


    @Autowired
    private CollectionFolderService collectionFolderService;


    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result doLogin(@Validated @RequestBody LoginDto loginDto) {

        String username = loginDto.getUsername();
        String password = loginDto.getPassword();


        String mobile = loginDto.getMobile();

        User user = null;
        switch (loginDto.getType()) {
            case 1 -> user = userService.loginByUserName(username, password);
            case 2 -> user = userService.loginByMobile(mobile, password);
            default -> {
                user = userService.loginByUserName(username, password);
            }
        }

        if (ObjectUtil.isNotNull(user)) {
            StpUtil.login(user.getId());
            return Result.ok();
        }
        return Result.error(ResultCode.NAME_PASS_ERROR);
    }

    @Operation(summary = "退出")
    @PostMapping("/logout")
    public Result doLogout() {
        StpUtil.logout();
        return Result.ok();
    }

    @PostMapping("/register")
    public Result register(@RequestBody @Validated(value = AddGroup.class) User user) {

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(6)));
        user.setCoin(0L);
        boolean save = userService.save(user);

        //初始化角色和默认收藏夹
        if (save) {
            Long id = user.getId();
            collectionFolderService.save(CollectionFolder.builder().userId(id).name("默认收藏夹").build());
            UserRole build = UserRole.builder().roleId(1L).userId(user.getId()).build();
            userRoleService.save(build);
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);
    }

    /**
     * 获取当前用户的信息
     * todo ，角色，权限等都要带上
     *
     * @return userinfo
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        long id = StpUtil.getLoginIdAsLong();

        System.out.println(id);
        User userInfo = userService.getUserInfo(id);
        return Result.success(userInfo);
    }

    /**
     * 根据iD获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Result getById(Long id) {
        User byId = userService.getById(id);
        byId.setPassword(null);
        return Result.success(byId);
    }

    /**
     * 根据姓名获取用户信息
     *
     * @param username
     * @return
     */
    @GetMapping("/username")
    public Result getByUserName(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("username", username);

        List<User> list = userService.list(userQueryWrapper);
        list.forEach(user -> user.setPassword(null));
        return Result.ok(list);
    }

    @GetMapping("/exists")
    public Result exists(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("u.username", username);
        boolean exists = userService.exists(userQueryWrapper);
        return Result.ok(exists);
    }


    @PostMapping("/putCoin")
    public Result putCoin(@RequestBody @Validated CoinDto coinDto) {
        boolean result = userService.putCoin(coinDto);
        return Result.success(result);
    }

}
