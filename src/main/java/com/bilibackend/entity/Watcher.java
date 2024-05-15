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

import java.util.Date;

/**
 * @Author 20126
 * @Description 进入直播间的时候保存到数据库中
 * @Date 2024/5/13 16:38
 * @Version 1.0
 */
@Data
@TableName("bili_watcher")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Watcher {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("stream")
    private String stream;

    /**
     * 直播观看者id
     */
    @TableField("userId")
    private Long userId;

    @TableField("client_id")
    private String client_id;


    @TableField("service_id")
    private String service_id;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("enterTime")
    private Date enterTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("leaveTime")
    private Date leaveTime;


    @TableField("ip")
    private String ip;
}
