package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bilibackend.validate.AddGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author 20126
 * @Description 描述
 * 1. 怎么记录完播数
 * 1.1 直接记录到表中，当前端触发条件（短视频播完，观看满5分钟）发起请求
 * 2. 怎么计算当前正在观看人数
 * 2.1 特点是动态更新变化较快
 * 3. 视频是要放到redis中的，动态的数据不记录（记录但不用），
 * 这些数据单独获取（打开的时候单独发请求）
 * （要加一个锁）
 * @Date 2024/5/14 12:04
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bili_video")
public class Video implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;


    /**
     * 视频标题·
     */
    @NotNull(groups = AddGroup.class)
    @TableField("title")
    private String title;

    /**
     * 是否删除，直接强制删除即可
     */
    @TableField("deleteStatus")
    private Integer deleteStatus;

//    中间表，多对多
    @TableField(exist = false)
    private List<User> publisher;


    /**
     * 视频url
     */
    @NotNull(groups = AddGroup.class)
    @TableField("url")
    private String url;

    /**
     * 发布日期
     */
    @TableField("createTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 完播量
     */
    @TableField("playTimes")
    private Long playTimes;

    /**
     * 合集id
     */
    @TableField("groupId")
    private Long groupId;

    /**
     * 评论数，这个可以不记录，需要单独查询的
     */
//    private Long comment;

    /**
     * 正在观看，不需要
     */
//    private Long watching;

    /**
     * 关键字
     */

    @TableField("keyWord")
    private String keyWord;


    @TableField("typeName")
    @NotNull(groups = AddGroup.class)
    private String typeName;

    /**
     * 转发数
     */
    @TableField("transmit")
    private Long transmit;

    @TableField("coin")
    private Long coin;

    @TableField("likeNumber")
    private Long likeNumber;


    @TableField("collect")
    private Long collect;

    /**
     * 视频简介
     */
    @TableField("description")
    private String description;

    /**
     * 视频封面
     */
    @TableField("cover")
    private String cover;


    /**
     * 视频时长，后期可以用于计算
     */
    @TableField("length")
    private Long length;

    /**
     * 评论
     */
    @TableField(exist = false)
    List<Comment> comment;

    /**
     * 表中不保存字段，查询详情的时候从redis获取
     */
    @TableField(exist = false)
    private Long watching;

    @TableField(exist = false)
    private String authors;
}
