package com.bilibackend.controller;

import com.bilibackend.entity.History;
import com.bilibackend.service.HistoryService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 20126
 * @Description 观看历史记录
 * @Date 2024/5/15 17:34
 * @Version 1.0
 */
@RestController
@RequestMapping("/history")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @PostMapping("/add")
    public Result addHistory(@RequestBody @Validated History history) {
        boolean save = historyService.save(history);
        if (save) {
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);
    }

    /**
     * 一次查询100条
     *
     * @param page 当前页数
     * @param size 分页大小
     * @param uid  用户id
     * @return Result
     */
    @GetMapping("/page")
    public Result queryPage(@RequestParam("page") Long page,
                            @RequestParam("size") Long size,
                            @RequestParam("uid") Long uid
    ) {
        return null;
    }

    @PostMapping("/search")
    public Result search(@RequestBody @Validated History history) {

        return null;
    }


    //todo 新增视频的时候，添加到redus中时也加一个50毫秒延迟
    @PostMapping("/update")
    public Result updateHistory(@RequestBody @Validated History history) {
//        history.
        return null;
    }
}
