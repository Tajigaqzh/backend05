package com.bilibackend.controller;

import com.bilibackend.dto.DynamicPageDto;
import com.bilibackend.entity.Dynamic;
import com.bilibackend.service.DynamicService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.validate.AddGroup;
import com.bilibackend.vo.PageResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author 20126
 * @Description 动态
 * @Date 2024/5/15 17:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    @PostMapping("/add")
    public Result addDynamic(@RequestBody @Validated(AddGroup.class) Dynamic dynamic) {
        dynamic.setCreateTime(new Date());
        boolean save = dynamicService.save(dynamic);
        if (save) return Result.ok();
        return Result.error(ResultCode.INSERT_ERROR);
    }

    @GetMapping("/delete")
    public Result delete(Long id) {
        boolean remove = dynamicService.removeById(id);
        if (remove) return Result.ok();
        return Result.error(ResultCode.DELETE_ERROR);
    }

    /**
     * 分页查询某一个
     *
     * @param dto
     * @return
     */
    @GetMapping("/byUserId")
    public Result getByUserId(@RequestBody DynamicPageDto dto) {
        PageResultVo pageResultVo = dynamicService.dynamicPage(dto.getPage(), dto.getSize(), dto.getUserId());
        return Result.ok(pageResultVo);
    }

    /**
     * 查询三天内的动态更新
     * 也是要分页查询，这个是为了通知
     * 会过滤掉
     * @return
     */
    @PostMapping("/recent")
    public Result allMyFollowDynamic(@RequestBody List<Long> ids) {
        List<Dynamic> dynamics = dynamicService.recentDynamic(ids);
        return Result.ok(dynamics);
    }

    @GetMapping("/history")
    public Result dynamicHistory(@RequestParam("id") Long id, @RequestParam("uid") Long uid) {
        dynamicService.addDynamicHistory(uid, id);
        return Result.ok();
    }
}
