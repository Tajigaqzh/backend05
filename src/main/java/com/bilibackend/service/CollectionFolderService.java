package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.CollectionFolder;
import com.bilibackend.vo.CollectionVo;

import java.util.List;

/**
* @author 20126
* @description 针对表【bili_collection_folder】的数据库操作Service
* @createDate 2024-05-16 17:00:11
*/
public interface CollectionFolderService extends IService<CollectionFolder> {

    boolean removeFolder(Long folderId);


    boolean batchMoveCollection(Long fromId, Long toId, List<Long> ids);


    List<CollectionVo> getCollectionsByFolderId(Long folderId);

}
