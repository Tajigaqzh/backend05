package com.bilibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.entity.History;
import com.bilibackend.service.HistoryService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.vo.PageResultVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        boolean save = historyService.addHistory(history);
        //更新视频的播放量
        if (save) return Result.ok();
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
                            @Valid @RequestParam("size") @Max(100) Long size,
                            @RequestParam("uid") Long uid
    ) {
        PageResultVo pageResultVo = historyService.historyPage(page, size, uid);
        return Result.success(pageResultVo);
    }

    /**
     * 根据标题，up主，时间搜索
     *
     * @param history
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody @Validated History history) {
        List<History> histories = historyService.searchCondition(history);
        return Result.success(histories);
    }


    //todo 新增视频的时候，添加到redus中时也加一个50毫秒延迟
    @PostMapping("/update")
    public Result updateHistory(@RequestBody @Validated History history) {

        boolean update = historyService.updateHistory(history);

        if (update) {
            return Result.success();
        }
        return Result.error(ResultCode.UPDATE_ERROR);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody List<Long> ids) {
        boolean b = historyService.removeBatchByIds(ids);
        if (b) return Result.success();
        return Result.error(ResultCode.DELETE_ERROR);
    }
}
