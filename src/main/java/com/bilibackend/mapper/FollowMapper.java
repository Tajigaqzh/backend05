package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibackend.entity.Follow;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 20126
* @description 针对表【bili_follow】的数据库操作Mapper
* @createDate 2024-05-16 13:25:44
* @Entity com.bilibackend.entity.Follow
*/
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

}




