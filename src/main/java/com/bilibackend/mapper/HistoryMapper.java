package com.bilibackend.mapper;

import com.bilibackend.entity.History;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 20126
* @description 针对表【bili_history】的数据库操作Mapper
* @createDate 2024-05-16 01:49:03
* @Entity com.bilibackend.entity.History
*/
@Mapper
public interface HistoryMapper extends BaseMapper<History> {

}




