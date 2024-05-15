package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author 20126
 * @Description 如何处理评论中的特殊情况？比如@某人、图片
 * <p>
 * 思路content中直接处理好，
 * <p>
 * 除了评论以外，头像连接
 * @Date 2024/5/15 0:23
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("bili_comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 视频id
     */
    @NotNull
    @TableField("videoId")
    private Long videoId;

    /**
     * 是否是置顶，默认0不置顶
     */
    @NotNull
    @TableField("isTop")
    private Integer isTop;

    //text，v-html渲染
    /**
     * 评论内容，v-html渲染，前端处理好直接存储到数据库
     * 存html标签
     */
    @NotNull
    @TableField("content")
    private String content;

    /**
     * 发布人姓名，存html标签
     */
    @NotNull
    @TableField("publishName")
    private String publishName;


    /**
     * 发布人头像图片
     */
    @NotNull
    @TableField("avatarImg")
    private String avatarImg;

    /**
     * 头像链接，跳转到个人主页
     */
    @NotNull
    @TableField("avatarLink")
    private String avatarLink;

    /**
     * 发部者会员等级
     */
    @NotNull
    @TableField("publishLevel")
    private Integer publishLevel;

    /**
     * 发布时间
     */
    @TableField("publishTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    /**
     * 父评论id
     */
    @TableField("parentId")
    private Long parentId;

    /**
     * ip地址
     */
    @TableField("ipAddress")
    private String ipAddress;


    /**
     * todo 需要根据ip地址查询 Java查询
     */
    @TableField("ipRegion")
    private String ipRegion;

    /**
     *
     */
    @TableField("agreeNumber")
    private Long agreeNumber;

    @TableField("disagreeNumber")
    private Long disagreeNumber;
}
