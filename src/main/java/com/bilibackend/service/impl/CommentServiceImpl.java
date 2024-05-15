package com.bilibackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.dto.AgreeDto;
import com.bilibackend.dto.CommentDeleteDto;
import com.bilibackend.entity.Comment;
import com.bilibackend.entity.VideoPublish;
import com.bilibackend.expection.DeleteException;
import com.bilibackend.mapper.CommentMapper;
import com.bilibackend.mapper.VideoPublishMapper;
import com.bilibackend.service.CommentService;
import com.bilibackend.utils.AddressUtils;
import com.bilibackend.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author 20126
 * @description 针对表【bili_comment】的数据库操作Service实现
 * @createDate 2024-05-15 02:23:30
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private VideoPublishMapper videoPublishMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;

    private static String commentLockPrefix = "comment:update:";


    /**
     * 获取评论人的Ip地址
     *
     * @param comment
     * @param request
     * @return
     */
    @Override
    public boolean addComment(Comment comment, HttpServletRequest request) {
        String ipAddr = IpUtils.getIpAddr(request);
        boolean isIp = IpUtils.isIP(ipAddr);
        if (isIp) {
            String realAddressByIP = AddressUtils.getRealAddressByIP(ipAddr);
            comment.setIpAddress(ipAddr);
            comment.setIpRegion(realAddressByIP);
        }
        comment.setIsTop(0);
        int insert = commentMapper.insert(comment);
        return insert != 0;
    }

    /**
     * 开放给用户的权限
     *
     * @param dto 作者可以删除本人发布视频下的所有评论
     *            发布人可以删除自己发布的评论
     *            还需要加一个接口，删除所有评论（给管理员用）
     * @return
     */
    @Override
    @Transactional
    public boolean deleteComment(CommentDeleteDto dto) {
        List<Long> ids = dto.getIds();
        String publishName = dto.getPublishName();
        Long videoId = dto.getVideoId();

        /**
         * 根据videoId和评论id删除
         */
        if (videoId != null) {
            long id = StpUtil.getLoginIdAsLong();

            //根据userId查videoId
            QueryWrapper<VideoPublish> videoPublishQueryWrapper = new QueryWrapper<>();
            videoPublishQueryWrapper.eq("publisherId", id);
            List<VideoPublish> videoPublishes = videoPublishMapper.selectList(videoPublishQueryWrapper);

            //此作者没有发布视频或者此作者发布的视频中没有此视频
            if (CollectionUtil.isEmpty(videoPublishes) || CollectionUtil.isEmpty(videoPublishes.stream().filter(videoPublish -> Objects.equals(videoPublish.getVideoId(), videoId)).toList())) {
                return false;
            }

            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("videoId", videoId).in("id", ids);
            int delete = commentMapper.delete(queryWrapper);
            if (delete == ids.size()) {
                return true;
            }
            throw new DeleteException();
        } else {
            /**
             * 根据publishId和评论id删除
             */
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("publishName", publishName).in("id", ids);
            int delete = commentMapper.delete(queryWrapper);
            if (delete == ids.size()) {
                return true;
            }
            throw new DeleteException();
        }
    }

    /**
     * 给管理员用
     *
     * @param ids 评论ids
     * @return
     */
    @Transactional
    public boolean deleteAllComment(List<Long> ids) {
        int i = commentMapper.deleteBatchIds(ids);
        if (i == ids.size()) {
            return true;
        }
        throw new DeleteException();
    }

    /**
     * 点赞以及取消点赞
     *
     * @param agreeDto
     * @return
     */
    @Override
    public Long agreeOrDisagreeComment(AgreeDto agreeDto) {
        Boolean agree = agreeDto.getAgree();
        Boolean disagree = agreeDto.getDisagree();

        if (ObjectUtil.isNotNull(agree)) {
            RLock lock = redissonClient.getLock(commentLockPrefix + "agree:" + agreeDto.getId());
            try {
                lock.lock();
                Comment comment = commentMapper.selectById(agreeDto.getId());
                Long agreeNumber = comment.getAgreeNumber();
                if (agree) {
                    agreeNumber = agreeNumber + 1;
                    comment.setAgreeNumber(agreeNumber);
                } else {
                    agreeNumber = agreeNumber - 1;
                    comment.setAgreeNumber(agreeNumber);
                }
                commentMapper.updateById(comment);
                return agreeNumber;
            } finally {
                lock.unlock();
            }
        } else if (ObjectUtil.isNotNull(disagree)) {
            RLock lock = redissonClient.getLock(commentLockPrefix + "disagree:" + agreeDto.getId());
            try {
                lock.lock();
                Comment comment = commentMapper.selectById(agreeDto.getId());
                Long disagreeNumber = comment.getDisagreeNumber();
                if (disagree) {
                    disagreeNumber = disagreeNumber + 1;
                    comment.setAgreeNumber(disagreeNumber);
                } else {
                    disagreeNumber = disagreeNumber - 1;
                    comment.setAgreeNumber(disagreeNumber);
                }
                commentMapper.updateById(comment);
                return disagreeNumber;
            } finally {
                lock.unlock();
            }
        }
        return null;
    }

    @Override
    public boolean upOrUnUpComment(Long id, Boolean isUp) {
        boolean result;
        if (isUp) {
            result = this.updateById(Comment.builder().id(id).isTop(1).build());
        } else {
            result = this.updateById(Comment.builder().id(id).isTop(0).build());
        }
        return result;
    }
}




