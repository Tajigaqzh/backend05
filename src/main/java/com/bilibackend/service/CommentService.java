package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.dto.AgreeDto;
import com.bilibackend.dto.CommentDeleteDto;
import com.bilibackend.entity.Comment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


/**
* @author 20126
* @description 针对表【bili_comment】的数据库操作Service
* @createDate 2024-05-15 02:23:30
*/
public interface CommentService extends IService<Comment> {

    boolean addComment(Comment comment, HttpServletRequest request);

    boolean deleteComment(CommentDeleteDto dto);

    boolean deleteAllComment(List<Long> ids);

    /**
     * 点赞以及取消点赞
     * @param agreeDto 点赞或取消
     * @return
     */
    Long agreeOrDisagreeComment(AgreeDto agreeDto);


    /**
     * 置顶或取消置顶
     * @param id
     * @param isUp
     */
    boolean upOrUnUpComment(Long id, Boolean isUp);
}
