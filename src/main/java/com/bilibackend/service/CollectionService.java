package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.Collection;

/**
* @author 20126
* @description 针对表【bili_collection】的数据库操作Service
* @createDate 2024-05-16 16:59:57
*/
public interface CollectionService extends IService<Collection> {

    boolean addCollection(Collection collection);
}
