package com.bilibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.entity.DanMu;
import com.bilibackend.service.DanMuService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.validate.AddGroup;
import com.bilibackend.validate.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/23 13:50
 * @Version 1.0
 */
@RestController
@RequestMapping("/danmu")
public class DanMuController {


    @Autowired
    private DanMuService danMuService;


    /**
     * 需要放行
     *
     * @param videoId
     * @return
     */
    @GetMapping("/get")
    public Result getByVideoId(@RequestParam("videoId") String videoId) {
        QueryWrapper<DanMu> danMuQueryWrapper = new QueryWrapper<>();
        danMuQueryWrapper.eq("videoId", videoId);
        List<DanMu> list = danMuService.list(danMuQueryWrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@Validated(AddGroup.class) @RequestBody DanMu danMu) {
        boolean save = danMuService.save(danMu);
        if (save) {
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);
    }

    @PostMapping("/update")
    public Result update(@Validated(UpdateGroup.class) @RequestBody DanMu danMu) {
        boolean b = danMuService.updateById(danMu);
        if (b) {
            return Result.ok();
        }
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    @GetMapping("/delete")
    public Result delete(Long id) {
        boolean b = danMuService.removeById(id);
        if (b) {
            return Result.ok();
        }
        return Result.error(ResultCode.DELETE_ERROR);
    }
}
