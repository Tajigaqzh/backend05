package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @TableName bili_video_type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "bili_video_type")
public class VideoType implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;


    @TableField("typeName")
    private String typeName;


    @TableField("isActive")
    private Boolean isActive;


    @TableField("description")
    private String description;
}