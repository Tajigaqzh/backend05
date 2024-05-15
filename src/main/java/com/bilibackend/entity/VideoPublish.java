package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/15 14:28
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@TableName("video_publish")
public class VideoPublish implements Serializable {

    @TableField("publisherId")
    private Long publisherId;

    @TableField("videoId")
    private Long videoId;
}
