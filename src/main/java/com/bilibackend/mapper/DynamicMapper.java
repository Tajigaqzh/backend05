package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibackend.entity.Dynamic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 20126
 * @description 针对表【bili_dynamic】的数据库操作Mapper
 * @createDate 2024-05-17 02:21:29
 * @Entity com.bilibackend.entity.Dynamic
 */
@Mapper
public interface DynamicMapper extends BaseMapper<Dynamic> {
    IPage<Dynamic> listPage(Page<Dynamic> page, @Param(Constants.WRAPPER) Wrapper<Dynamic> ew);
}




