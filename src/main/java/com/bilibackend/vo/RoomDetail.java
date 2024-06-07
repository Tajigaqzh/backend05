package com.bilibackend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bilibackend.validate.AddGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 20126
 * @Description
 * @Date 2024/6/1 23:21
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetail implements Serializable {

    private Long startId;


    /**
     * 房间的uuid，后端生成
     */
    private String roomId;


    private int deleteStatus;



    private String roomTitle;


    private String roomDesc;



    private String typeTitle;


    private String roomCover;



    private String notice;


    /**
     * 创建直播间时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 直播结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


    private String playUrl;

    private String username;

    private String avatar;
}
