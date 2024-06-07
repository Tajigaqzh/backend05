package com.bilibackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.Dynamic;
import com.bilibackend.mapper.DynamicMapper;
import com.bilibackend.service.DynamicService;
import com.bilibackend.utils.HpDateUtils;
import com.bilibackend.vo.PageResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 20126
 * @description 针对表【bili_dynamic】的数据库操作Service实现
 * @createDate 2024-05-17 02:21:29
 */
@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic>
        implements DynamicService {


    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public PageResultVo dynamicPage(Long page, Long size, Long uid) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publisherId", uid);
        IPage<Dynamic> dynamicIPage = dynamicMapper.listPage(new Page<>(page, size), queryWrapper);

        //近5~8天

        List<String> redisKeyWithPrefix = HpDateUtils.getLatestLastRedisKeyWithPrefix(4, 1, "watched:dynamic:" + uid + ":");

        /**
         * 从redis中获取
         */
        List<Long> redisKeys = new ArrayList<>();
        redisKeyWithPrefix.forEach(key -> {
            List<Long> range = (List<Long>) (List<?>) redisTemplate.opsForList().range(key, 0, -1);
            if (CollectionUtil.isNotEmpty(range)) redisKeys.addAll(range);
        });

        /**
         * 标记近5~8天是否看过
         */
        if (CollectionUtil.isNotEmpty(redisKeys)) {
            dynamicIPage.getRecords().forEach(dynamic -> {
                boolean contains = redisKeys.contains(dynamic.getId());
                dynamic.setIsWatched(contains);
            });
        }

        return PageResultVo.builder().total(dynamicIPage.getTotal()).current(dynamicIPage.getCurrent()).data(dynamicIPage.getRecords()).build();
    }

    /**
     * @param ids
     * @return
     */
    //todo 添加到redis中以及定时任务替换的时候要加锁  ？？ 这个可重复，添加的时候不必加锁，不替换，直接按照日期设置过期时间
    public List<Dynamic> recentDynamic(List<Long> ids) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("publisherId", ids);
        queryWrapper.ge("createTime", DateUtil.offsetDay(new Date(), -3));
        long id = StpUtil.getLoginIdAsLong();
        List<Dynamic> dynamics = dynamicMapper.selectList(queryWrapper);

        //动态一周不看就从redis中清除，创建的时候设置过期时间
//        List<String> redisDateKey = HpDateUtils.getRedisDateKey(-7, "watched:dynamic:" + id + ":");

        List<String> redisKeyWithPrefix = HpDateUtils.getLatestLastRedisKeyWithPrefix(4, 1, "watched:dynamic:" + id + ":");

        /**
         * 从redis中获取
         */
        List<Long> redisKeys = new ArrayList<>();
        redisKeyWithPrefix.forEach(key -> {
            List<Long> range = (List<Long>) (List<?>) redisTemplate.opsForList().range(key, 0, -1);
            if (CollectionUtil.isNotEmpty(range)) redisKeys.addAll(range);
        });

        if (CollectionUtil.isNotEmpty(redisKeys)) {
            return dynamics.stream().filter(dynamic -> !redisKeys.contains(dynamic.getId())).toList();
        }

        return dynamics;
    }

    public void addDynamicHistory(Long userId, Long id) {
        String key = HpDateUtils.getLatestLastRedisKeyWithPrefix(4, 0, "watched:dynamic:" + id + ":").get(0);
        Boolean hasKey = redisTemplate.hasKey(key);
        redisTemplate.opsForList().rightPush(key, id);

        if (hasKey == null || !hasKey) {
            //过期时间多设置两天
            redisTemplate.expire(key, 10, TimeUnit.DAYS);
        }
    }
}




