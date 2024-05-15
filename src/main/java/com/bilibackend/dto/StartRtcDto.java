package com.bilibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author 20126
 * @Description
 * @Date 2024/5/10 18:55
 * @Version 1.0
 * 发起人id，房间uuid，房间标题，房间简介，房间关键字，公告，sdp，弹幕？，此时还要创建一个room群私聊
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartRtcDto {
    /**
     * 发起人id
     */
    @NotNull
    private Long startId;


    /**
     * 直播间标题
     */
    @NotBlank
    private String roomTitle;

    private String roomId;

    private String roomCover;

    /**
     * 房间简介信息
     */
    private String roomDesc;

    /**
     * 房间分类，用于检索
     */
    private String typeTitle;

    /**
     * 房间公告
     */
    private String notice;

    /**
     * 发起人sdp
     */
    @NotBlank
    private String sdp;

}
