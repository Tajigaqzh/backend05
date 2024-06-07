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

/**
 * 不设置更新了，删除就行
 *
 * @TableName bili_dynamic
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "bili_dynamic")
public class Dynamic implements Serializable {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @TableField("createTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 发布者id
     */
    @TableField("publisherId")
    @NotNull(groups = AddGroup.class)
    private Long publisherId;

    /**
     * 图片和文字，前端决定怎么保存
     */
    @TableField("content")
    private String content;

    /**
     * 如果有视频，保存到这个。累哦为空
     */
    @TableField("videoId")
    private Long videoId;

    @TableField(exist = false)
    private Boolean isWatched;
}