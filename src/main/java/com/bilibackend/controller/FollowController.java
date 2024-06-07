package com.bilibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.dto.FollowDto;
import com.bilibackend.entity.Follow;
import com.bilibackend.entity.User;
import com.bilibackend.service.FollowService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author 20126
 * @Description 关注
 * @Date 2024/5/15 17:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    //查询，关注，取消关注

    @Autowired
    private FollowService followService;


    @GetMapping("/followMe")
    public Result getFollowMe(Long upId) {

        List<User> follows = followService.getListByUpId(upId);
        return Result.success(follows);
    }

    @GetMapping("/myFollow")
    public Result getMyFollow(Long uid) {
        List<User> follows = followService.getListByUserId(uid);
        return Result.success(follows);
    }


    @PostMapping("/followOrUnFollow")
    public Result followOrUnFollowUp(@RequestBody @Validated FollowDto followDto) {
        Boolean isFollow = followDto.getIsFollow();
        boolean result;
        if (isFollow) {
            result = followService.save(Follow.builder().followTime(new Date()).upId(1L).userId(1L).build());
        } else {
            QueryWrapper<Follow> followQueryWrapper = new QueryWrapper<>();
            followQueryWrapper.eq("upId", followDto.getUpId()).eq("userId", followDto.getUserId());
            result = followService.remove(followQueryWrapper);
        }
        if (result) {
            return Result.ok();
        }
        return Result.error(ResultCode.UPDATE_ERROR);

    }


}
