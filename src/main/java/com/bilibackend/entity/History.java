package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 0:55
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("bili_history")
public class History implements Serializable {
    /**
     * 历史记录id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 视频标题
     */
    @TableField("title")
    private String title;

    /**
     * 记录的时间，当用户完成播放，中途退出播放的时候记录,要根据这个排序
     */
    @TableField("recordTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recordTime;

    /**
     * 用户id，要加索引
     */
    private Long uid;

    /**
     * 已看完的时长，秒数，完成后记录为一个固定值，比如-1
     */
    private Long watched;

    /**
     * 视频类型
     */
    private String videoType;
    /**
     * 视频url
     */
    private String url;

    /**
     * 封面url
     */
    private String coverUrl;

    /**
     * 发布者头像
     */
    private String authorAvatar;

    /**
     * 发布者姓名
     */
    private String authorName;

    /**
     * 发布者主页
     */
    private String authorLink;

    /**
     * 是否是手机端
     */
    private String isMobile;
}
