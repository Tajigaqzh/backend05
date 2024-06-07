package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibackend.entity.History;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 20126
* @description 针对表【bili_history】的数据库操作Mapper
* @createDate 2024-05-16 01:49:03
* @Entity com.bilibackend.entity.History
*/
@Mapper
public interface HistoryMapper extends BaseMapper<History> {


    IPage<History> listPage(Page<History> page, @Param(Constants.WRAPPER) Wrapper<History> ew);

}




