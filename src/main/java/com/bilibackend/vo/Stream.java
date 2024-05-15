package com.bilibackend.vo;

import com.bilibackend.entity.LiveRoom;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 13:01
 * @Version 1.0
 */
@Data
public class Stream implements Serializable {
    /**
     * stream Id
     */
    private String id;
    /**
     * 发起时传递的名字
     */
    private String name;
    /**
     * vhost
     */
    private String vhost;
    /**
     * 所属的app，当前是live
     */
    private String app;
    private String tcUrl;
    /**
     * 播放url app+name
     */
    private String url;

    /**
     * 已传输毫秒数
     */
    private Long live_ms;
    /**
     * 在线用户数
     */
    private Integer clients;

    private Integer frames;

    private Integer send_bytes;

    private Integer recv_bytes;

    private VideoKbps kbps;

    private Publish publish;

    private SrsVideo video;

    private SrsAudio audio;

    private LiveRoom liveRoom;
}
