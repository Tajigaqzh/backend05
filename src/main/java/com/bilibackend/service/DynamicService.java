package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.Dynamic;
import com.bilibackend.vo.PageResultVo;

import java.util.List;

/**
 * @author 20126
 * @description 针对表【bili_dynamic】的数据库操作Service
 * @createDate 2024-05-17 02:21:29
 */
public interface DynamicService extends IService<Dynamic> {
    PageResultVo dynamicPage(Long page, Long size, Long uid);

    List<Dynamic> recentDynamic(List<Long> ids);

    void addDynamicHistory(Long userId, Long id);
}
