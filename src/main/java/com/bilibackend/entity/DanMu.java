package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bilibackend.validate.AddGroup;
import com.bilibackend.validate.UpdateGroup;
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
 * @Author 20126
 * @Description
 * @Date 2024/5/23 13:47
 * @Version 1.0
 */
@TableName(value = "bili_danmu")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanMu implements Serializable {
    /**
     * 弹幕记录id
     */

    @NotNull(groups = UpdateGroup.class)
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 视频id
     */
    @NotNull(groups = AddGroup.class)
    @TableField("videoId")
    private Long videoId;

    @TableField("time")
    private Long time;

    @TableField("createTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 用户id
     */
    @TableField("userId")
    @NotNull(groups = AddGroup.class)
    private Long userId;

    /**
     * 弹幕详情字符串
     */
    @TableField("detail")
    @NotNull
    private String detail;

}
