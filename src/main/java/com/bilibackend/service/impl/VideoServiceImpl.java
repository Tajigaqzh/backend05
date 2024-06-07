package com.bilibackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.document.VideoDocument;
import com.bilibackend.dto.AuthorDto;
import com.bilibackend.dto.VideoDto;
import com.bilibackend.dto.VideoUpdateDto;
import com.bilibackend.entity.History;
import com.bilibackend.entity.User;
import com.bilibackend.entity.Video;
import com.bilibackend.entity.VideoPublish;
import com.bilibackend.mapper.VideoMapper;
import com.bilibackend.repository.VideoRepository;
import com.bilibackend.service.HistoryService;
import com.bilibackend.service.OssService;
import com.bilibackend.service.VideoPublishService;
import com.bilibackend.service.VideoService;
import com.bilibackend.utils.ConvertUtil;
import com.bilibackend.utils.HpDateUtils;
import com.bilibackend.vo.PageResultVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/14 14:58
 * @Version 1.0
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    //todo 动态，转发,转发，更新完播率,
    //评论点赞，


    @Autowired
    private VideoRepository videoRepository;


    @Autowired
    private VideoPublishService videoPublishService;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    @Autowired
    private HistoryService historyService;

    @Autowired
    private OssService ossService;

    /**
     * recommend没有：
     */
    /**
     * data,推荐视频
     */
    private static String recommendPrefix = "video:recommend";

    /**
     * data,推荐视频
     */
    private static String recommendTypePrefix = "video:type:";


    /**
     * 锁，点赞的锁
     */
    private static String zanLockPrefix = "zan:";
    /**
     * 锁，锁定更新数据表中的转发数量
     */
    private static String transmitLockPredix = "lock:transmit:";

    /**
     * 锁，锁定更新数据表中的playTimes
     */
    private static String playTimeLockPrefix = "lock:playTime:";

    /**
     * 锁，锁定视频的正在观看次数
     */
    private static String detailLockPrefix = "detail:lock:";

    /**
     * data,某个视频的正在观看人数，过期时间3小时，如果有则继续新增
     * 请求视频详情的时候新增，用户点击返回或者退出，或者完成播放的时候减少
     */
    private static String detailCount = "detail:count:";
    /**
     * 方案1.version
     * 方案2.Redission
     * 用redis锁的话，每个视频都需要一个锁，并发上千
     * 在设置在线人数的时候也用redis锁
     */

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 转发要区分不同情况，比如站内转发到动态，站外转发生成链接，这里是为了掩饰
     *
     * @param id todo 转发要分情况处理，这里简单示例演示redission锁的应用
     */

    public boolean updateTransmit(Long id) {
        RLock lock = redissonClient.getLock(transmitLockPredix + id);
        lock.lock();
        try {
            Video video = baseMapper.selectById(id);
            if (ObjectUtil.isNotNull(video)) {
                int i = baseMapper.updateById(Video.builder().id(id).transmit(video.getTransmit() + 1).build());
                return i > 0;
            }
        } finally {
            lock.unlock();
        }

        return false;
    }


    /**
     * 这个调用频率也非常高
     * todo 过滤
     *
     * @param page
     * @param size
     * @return
     */
    public PageResultVo recommendPage(Long page, Long size) {
        //推荐中的内容
        Long remain = redisTemplate.opsForList().size(recommendPrefix);

        Object loginIdDefaultNull = StpUtil.getLoginIdDefaultNull();

        //获取用户id
        Long loginId = null;
        if (loginIdDefaultNull != null) {
            loginId = Long.parseLong(loginIdDefaultNull.toString());
        }
        //推荐的视频
        List<Video> recommend = null;

        //根据浏览历史推荐
        if (loginId != null) {
            Long size1 = redisTemplate.opsForList().size("history:recommend:" + loginId);
            if (size1 != null) {
                if (size1 >= 3) {
                    recommend = (List<Video>) (List<?>) redisTemplate.opsForList().rightPop("history:recommend:" + loginId, 3);
                }
            }
        }

        //从数据库中查询
        if (remain == null || remain < 100 || size * (page + 1) > remain) {
            //从数据库分页查询
            QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();

            //一周之前，以及15天之前到现在播放超过3000的
            long lastWeek = DateUtil.lastWeek().getTime();

            long lastTwoWeek = DateUtil.offsetDay(new Date(), -15).getTime();
            videoQueryWrapper.ge("v.createTime", lastWeek).orderByAsc("v.createTime").or(videoQueryWrapper1 -> {
                videoQueryWrapper1.ge("v.createTime", lastTwoWeek).and(videoQueryWrapper2 -> {
                    videoQueryWrapper2.ge("v.playTimes", 3000);
                });
            });
            IPage<Video> videoIPage = videoMapper.listPage(new Page<>(page, size), videoQueryWrapper);

            List<Video> records = videoIPage.getRecords();
            if (recommend != null) {
                records.addAll(recommend);
            }

            List<Video> videos = filterWatched(records);
            return PageResultVo.builder().total(videoIPage.getTotal()).current(videoIPage.getCurrent()).data(videos).build();
        }

        List<Video> range = (List<Video>) (List<?>) redisTemplate.opsForList().range(recommendPrefix, size * page, size * (page + 1));

        //推荐算法推荐的视频
        if (recommend != null && range != null) {
            range.addAll(recommend);
        }

        return PageResultVo.builder().total(remain).current(page).data(filterWatched(range)).build();
    }

    /**
     * 过滤近14（约数，不是确切地14天）天是否看过
     *
     * @param beforeFilter
     * @return
     */
    private List<Video> filterWatched(List<Video> beforeFilter) {
        Object loginIdDefaultNull = StpUtil.getLoginIdDefaultNull();


        if (loginIdDefaultNull != null) {
            Long loginId = Long.parseLong(loginIdDefaultNull.toString());
            List<String> redisKeyWithPrefix = HpDateUtils.getLatestLastRedisKeyWithPrefix(7, 2, "watched:video:" + loginId + ":");
            List<Long> watchedIds = new ArrayList<>();
            redisKeyWithPrefix.forEach(redisKey -> {
                List<Long> range = (List<Long>) (List<?>) redisTemplate.opsForList().range(redisKey, 0, -1);
                if (range != null) watchedIds.addAll(range);
            });
            return beforeFilter.stream().filter(video -> !watchedIds.contains(video.getId())).toList();
        }
        return beforeFilter;
    }

    @Override
    public PageResultVo recommendTypePage(Long page, Long size, String type) {

        Long remain = redisTemplate.opsForList().size(recommendTypePrefix + type);

        if (remain == null || remain < 100 || size * (page + 1) > remain) {
            //从数据库分页查询
            QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();

            //一周之前，以及15天之前到现在播放超过3000的
            long lastWeek = DateUtil.lastWeek().getTime();

            long lastTwoWeek = DateUtil.offsetDay(new Date(), -15).getTime();
            videoQueryWrapper.ge("v.createTime", lastWeek).eq("v.typeName", type).orderByAsc("v.createTime").or(videoQueryWrapper1 -> videoQueryWrapper1.ge("v.createTime", lastTwoWeek).and(videoQueryWrapper2 -> videoQueryWrapper2.ge("v.playTimes", 3000).eq("v.typeName", type)));
            IPage<Video> videoIPage = videoMapper.listPage(new Page<>(page, size), videoQueryWrapper);


            videoIPage.setRecords(filterWatched(videoIPage.getRecords()));

            return PageResultVo.builder().total(videoIPage.getTotal()).current(videoIPage.getCurrent()).data(videoIPage.getRecords()).build();
        }

        List<Video> range = (List<Video>) (List<?>) redisTemplate.opsForList().range(recommendTypePrefix + type, size * page, size * (page + 1));
        //过滤
        return PageResultVo.builder().total(remain).current(page).data(filterWatched(range)).build();
    }


    @Override
    public Video getDetailById(Long videoId) {
        //如果是登录状态，添加到redis缓存中
        String id = StpUtil.getLoginIdDefaultNull().toString();

        Long loginId = Long.parseLong(id);

        Video video = videoMapper.getDetail(videoId);
        if (loginId != null) {

            addToCacheSingle(videoId);

            List<User> publisher = video.getPublisher();
            String name = null;
            if (publisher.size() == 0) {
                name = "";
            } else if (publisher.size() == 1) {
                name = publisher.get(0).getUsername();

            } else {
                name = name + "等";
            }
            History build;
            if (video.getPublisher().size() > 0) {
                build = History.builder().uid(loginId).videoId(videoId).authorAvatar(video.getPublisher().get(0).getAvatar())
                        .authorName(name)
                        .recordTime(new Date())
                        .url(video.getUrl())
                        .videoType(video.getTypeName())
                        .coverUrl(video.getCover())
                        .title(video.getTitle())
                        .watched(1000L).build();
            } else {
                build = History.builder().uid(loginId).videoId(videoId).authorAvatar("")
                        .authorName(name)
                        .recordTime(new Date())
                        .url(video.getUrl())
                        .videoType(video.getTypeName())
                        .coverUrl(video.getCover())
                        .title(video.getTitle())
                        .watched(1000L).build();
            }


            historyService.addHistory(build);
        }


        if (ObjectUtil.isNotNull(video)) {
            RLock lock = redissonClient.getLock(detailLockPrefix + videoId);
            Long count;
            lock.lock();
            try {
                //正在观看人数，todo 离开的时候减少
                count = redisTemplate.opsForValue().increment(detailCount + videoId);
                redisTemplate.expire(detailCount + videoId, 3, TimeUnit.HOURS);
            } finally {
                lock.unlock();
            }
            video.setWatching(count);
            return video;
        }


        return null;
    }

    @Override
    @Transactional
    public boolean saveVideo(VideoDto videoDto) {
        Video build = Video.builder()
                .cover(videoDto.getCover())
                .comment(null)
                .createTime(new Date())
                .url(videoDto.getUrl())
                .groupId(videoDto.getGroupId())
                .title(videoDto.getTitle())
                .keyWord(videoDto.getKeyWord())
                .length(videoDto.getLength())
                .collect(0L)
                .playTimes(0L).coin(0L).likeNumber(0L).transmit(0L).deleteStatus(1)
                .typeName(videoDto.getTypeName()).description(videoDto.getDescription()).build();
        int insert = videoMapper.insert(build);

        if (insert > 0) {

            List<Long> ids = videoDto.getAuthors().stream().map(AuthorDto::getId).toList();

            List<VideoPublish> videoPublishes = ids.stream().map(id -> VideoPublish.builder().publisherId(id).videoId(build.getId()).build()).toList();
            boolean b = videoPublishService.saveBatch(videoPublishes);
            if (!b) {
                return false;
            }

            List<String> list = videoDto.getAuthors().stream().map(AuthorDto::getName).toList();

            String reduce = list.stream().reduce("", (s, item) -> s + item + ",");
            build.setAuthors(reduce);

            VideoDocument videoDocument = ConvertUtil.convertToDocument(build);
            try {
                videoRepository.save(videoDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //加入到redis中的推荐和分类推荐中
            redisTemplate.boundListOps(recommendPrefix).leftPush(build);
            redisTemplate.boundListOps(recommendTypePrefix + videoDto.getTypeName()).leftPush(build);

            return true;
        }

        return false;
    }


    private void addToCacheSingle(Long id) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(id);
        this.addToCache(list);
    }

    @Override
    public void addToCache(List<Long> ids) {
        String id = StpUtil.getLoginIdDefaultNull().toString();

        Long loginId = Long.parseLong(id);
        if (loginId != null) {
            String key = HpDateUtils.getLatestLastRedisKeyWithPrefix(7, 0, "watched:video:" + loginId + ":").get(0);
            Boolean hasKey = redisTemplate.hasKey(key);
            redisTemplate.opsForList().rightPush(key, ids.toArray());
            if (hasKey == null || !hasKey) {
                //过期时间多设置两天
                redisTemplate.expire(key, 15, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public boolean removeVideo(Long id) {

        Video video = videoMapper.selectById(id);
        if (ObjectUtil.isNull(video)) return false;
//        封面url
        String cover = video.getCover();

        //视频的url
        String url = video.getUrl();
        String md5 = ossService.getMd5(url);
        int i = videoMapper.deleteById(id);
        if (i > 0) {
            ossService.deleteFile(cover, null);
            //这个也会删除redis缓存的url
            ossService.deleteFile(url, md5);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateVideo(VideoUpdateDto videoUpdateDto) {
        Video build = Video.builder()
                .id(videoUpdateDto.getId())
                .title(videoUpdateDto.getTitle())
                .groupId(videoUpdateDto.getGroupId())

                .keyWord(videoUpdateDto.getKeyWord())
                .description(videoUpdateDto.getDescription()).build();
        String typeNameNew = videoUpdateDto.getTypeName();
        //更新分类
        if (typeNameNew != null) {

            build.setTypeName(typeNameNew);

            Video video = videoMapper.selectById(videoUpdateDto.getId());
            String typeNameOld = video.getTypeName();
            video.setWatching(null);
            video.setCoin(null);
            video.setCollect(null);
            video.setPlayTimes(null);
            video.setTransmit(null);
            video.setCoin(null);
            video.setLikeNumber(null);


            try {
                //修改的时候同步修改es，因为id加了索引，所以直接插入就是替换
                videoRepository.save(ConvertUtil.convertToDocument(video));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            redisTemplate.opsForList().remove(recommendTypePrefix + typeNameOld, 1, video);
            video.setTypeName(videoUpdateDto.getTypeName());
            //todo，极小概率事件定时任务执行刷新的空隙leftPush放到最后面了
            Long size = redisTemplate.opsForList().size(recommendTypePrefix + typeNameNew);
            //应对极端情况
            if (size == null || size == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    redisTemplate.opsForList().leftPush(recommendTypePrefix + typeNameNew, video);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                redisTemplate.opsForList().leftPush(recommendTypePrefix + typeNameNew, video);
            }
        }
        int i = videoMapper.updateById(build);
        return i > 0;
    }

    @Override
    public boolean zan(Long videoId, boolean isAdd) {

        RLock lock = redissonClient.getLock(zanLockPrefix + videoId);
        lock.lock();
        //查询之前加锁
        Video video = videoMapper.selectById(videoId);
        Video build = null;
        try {
            if (isAdd) {
                build = Video.builder().id(videoId).likeNumber(video.getLikeNumber() + 1).build();
            } else {
                if (video.getLikeNumber() >= 1) {
                    build = Video.builder().id(videoId).likeNumber(video.getLikeNumber() - 1).build();
                }
            }
            if (build != null) {
                int i = videoMapper.updateById(build);
                return i > 0;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }


    /**
     * 触发时机，完成播放，退出视频
     * todo 结合观看历史
     *
     * @param vid
     */

    public void decrementCount(Long vid) {
        RLock lock = redissonClient.getLock(detailLockPrefix + vid);
        lock.lock();
        try {
            Long count = (Long) redisTemplate.opsForValue().get(detailCount + vid);
            if (ObjectUtil.isNull(count) || count == 1) {
                redisTemplate.delete(detailCount + vid);
            } else {
                redisTemplate.opsForValue().decrement(detailCount + vid);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 完成播放 todo 记录历史记录，decrement到0以后会出现负数，所以要先查再减少
     */
    public void completePlay(Long id) {
        RLock lock = redissonClient.getLock(playTimeLockPrefix + id);
        lock.lock();
    }

    /**
     * 这个调用的频率较高
     * todo插入历史记录 完播率
     *
     * @param id 更新播放次数，完成播放后，还要插入历史记录
     * @return
     */
    public boolean updatePlayTimes(Long id) {
        RLock lock = redissonClient.getLock(playTimeLockPrefix + id);
        lock.lock();

        try {
            Video video = baseMapper.selectById(id);
            if (ObjectUtil.isNotNull(video)) {
                int i = baseMapper.updateById(Video.builder().id(id).playTimes(video.getPlayTimes() + 1).build());
                return i > 0;
            }
        } finally {
            lock.unlock();
        }

        return false;
    }

    public List<Video> searchPge(String keyword, Integer page, Integer size) {
        List<VideoDocument> byRelation = videoRepository.findByRelation(keyword, PageRequest.of(page, size));
        if (CollectionUtil.isNotEmpty(byRelation)) {
            return byRelation.stream().map(ConvertUtil::convertToVideo).toList();
        }
        return null;
    }
}
