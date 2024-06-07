package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 12:51
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getUserInfo(@Param("queryId") Long queryId);

    List<User> getFollowMe(@Param("upId") Long upId);

    List<User> getMyFollow(@Param("userId") Long userId);

}
