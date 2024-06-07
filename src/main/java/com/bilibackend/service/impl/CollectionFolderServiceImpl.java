package com.bilibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.Collection;
import com.bilibackend.entity.CollectionFolder;
import com.bilibackend.expection.DeleteException;
import com.bilibackend.mapper.CollectionFolderMapper;
import com.bilibackend.service.CollectionFolderService;
import com.bilibackend.service.CollectionService;
import com.bilibackend.vo.CollectionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 20126
 * @description 针对表【bili_collection_folder】的数据库操作Service实现
 * @createDate 2024-05-16 17:00:11
 */
@Service
public class CollectionFolderServiceImpl extends ServiceImpl<CollectionFolderMapper, CollectionFolder>
        implements CollectionFolderService {

    @Autowired
    private CollectionService collectionService;

    @Override
    @Transactional
    public boolean removeFolder(Long folderId) {
        QueryWrapper<Collection> collectionQueryWrapper = new QueryWrapper<>();
        collectionQueryWrapper.eq("folderId", folderId);

        boolean collection = collectionService.remove(collectionQueryWrapper);

        boolean folder = this.removeById(folderId);

        if (collection && folder) {
            return true;
        }
        throw new DeleteException();
    }

    @Override
    @Transactional
    public boolean batchMoveCollection(Long fromId, Long toId, List<Long> ids) {
        QueryWrapper<Collection> collectionQueryWrapper1 = new QueryWrapper<>();
        collectionQueryWrapper1.eq("folderId", fromId).in("videoId", ids);
        boolean remove = collectionService.remove(collectionQueryWrapper1);

        ArrayList<Collection> collections = new ArrayList<>();

        ids.forEach(id -> {
            Collection build = Collection.builder().videoId(id).folderId(toId).build();
            collections.add(build);

        });
        boolean save = collectionService.saveBatch(collections);

        if (remove && save) {
            return true;
        }
        throw new DeleteException();
    }

    @Override
    public List<CollectionVo> getCollectionsByFolderId(Long folderId) {
        return this.getBaseMapper().getVide0ByFolderId(folderId);
    }
}




