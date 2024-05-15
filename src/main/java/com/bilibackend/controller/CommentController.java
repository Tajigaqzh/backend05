package com.bilibackend.controller;

import com.bilibackend.dto.AgreeDto;
import com.bilibackend.dto.CommentDeleteDto;
import com.bilibackend.entity.Comment;
import com.bilibackend.service.CommentService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 20126
 * @Description 需要登录
 * @Date 2024/5/15 12:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/add")
    public Result add(@RequestBody @Validated Comment comment, HttpServletRequest request) {
        boolean save = commentService.addComment(comment, request);
        if (save) {
            return Result.ok();
        }
        return Result.error(ResultCode.INSERT_ERROR);

    }

    /**
     * 删除评论，有两种方式，根据
     * ids require Array
     * - publishName 发布人姓名（自己删除自己发的）
     * - videoId  根据视频id和id删除
     * - 前端也要鉴权，后端也要鉴权
     *
     * @param commentDeleteDto
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@RequestBody CommentDeleteDto commentDeleteDto) {
        boolean b = commentService.deleteComment(commentDeleteDto);
        if (b) {
            return Result.success();
        }
        return Result.error(ResultCode.DELETE_ERROR);
    }

    /**
     * 加一层吧,只需要传递ids
     *
     * @param commentDeleteDto
     * @return
     */
    @PostMapping("/deleteAll")
    public Result deleteAll(@RequestBody CommentDeleteDto commentDeleteDto) {
        boolean b = commentService.deleteAllComment(commentDeleteDto.getIds());
        if (b) {
            return Result.success();
        }
        return Result.error(ResultCode.DELETE_ERROR);
    }


    /**
     * 赞同或者是反对
     *
     * @return
     */
    @PostMapping("/update")
    public Result updateComment(@RequestBody @Validated AgreeDto agreeDto) {
        Long aLong = commentService.agreeOrDisagreeComment(agreeDto);
        return Result.ok(aLong);
    }

    /**
     * 置顶或取消置顶
     *
     * @param id id
     * @return
     */
    @GetMapping("/upOrUnUp")
    public Result upOrUnUpComment(@Validated @NotNull Long id, Boolean isUp) {
        boolean update = commentService.upOrUnUpComment(id, isUp);
        if (update) {
           return Result.ok();
        }
        return Result.error(ResultCode.UPDATE_ERROR);
    }
}
