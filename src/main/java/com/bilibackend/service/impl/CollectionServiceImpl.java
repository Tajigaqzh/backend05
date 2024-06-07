package com.bilibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.Collection;
import com.bilibackend.entity.Video;
import com.bilibackend.mapper.CollectionMapper;
import com.bilibackend.mapper.VideoMapper;
import com.bilibackend.service.CollectionService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 20126
 * @description 针对表【bili_collection】的数据库操作Service实现
 * @createDate 2024-05-16 16:59:57
 */
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
        implements CollectionService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 修改收藏数的锁
     */
    private static final String collectionLock = "video:collection:";

    @Override
    public boolean addCollection(Collection collection) {
        RLock lock = redissonClient.getLock(collectionLock + collection.getVideoId());
        lock.lock();

        try {
            Video video = videoMapper.selectById(collection.getVideoId());
            Video build = Video.builder().id(video.getId()).collect(video.getCollect() + 1).build();
            int i = videoMapper.updateById(build);
            boolean save = this.save(collection);
            return i > 0 && save;
        } finally {
            lock.unlock();
        }
    }
}




