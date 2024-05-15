package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibackend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 20126
 * @description 针对表【bili_comment】的数据库操作Mapper
 * @createDate 2024-05-15 02:23:30
 * @Entity generator.domain.BiliComment
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}




