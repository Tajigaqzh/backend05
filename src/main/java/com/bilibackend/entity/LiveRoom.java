package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bilibackend.validate.AddGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author 20126
 * @Description 创建后不能修改
 * @Date 2024/5/12 10:30
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "bili_live_room")
public class LiveRoom {

    /**
     * 发起人id
     */
    @NotNull(groups = AddGroup.class)
    @TableField("startId")
    private Long startId;


    /**
     * 房间的uuid，后端生成
     */
    @TableId(type = IdType.ASSIGN_UUID, value = "roomId")
    private String roomId;


    @TableField("deleteStatus")
    private int deleteStatus;


    /**
     * 直播间标题
     */
    @NotBlank
    @TableField("roomTitle")
    private String roomTitle;

    /**
     * 房间简介信息
     */
    @TableField("roomDesc")
    private String roomDesc;


    /**
     * 房间分类，用于检索，
     */
    @NotNull
    @TableField("typeTitle")
    private String typeTitle;


    @TableField("roomCover")
    private String roomCover;


    /**
     * 房间公告
     */
    @TableField("notice")
    private String notice;


    /**
     * 创建直播间时间
     */
    @TableField("createTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 直播结束时间
     */
    @TableField("endTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 播放url
     */
    @TableField("playUrl")
    private String playUrl;
}
