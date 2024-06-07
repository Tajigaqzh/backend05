package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName bili_follow
 */
@TableName(value = "bili_follow")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follow implements Serializable {
    /**
     *
     */
    @TableField("userId")
    private Long userId;


    /**
     *
     */
    @TableField("upId")
    private Long upId;

    /**
     *
     */
    @TableField("followTime")
    private Date followTime;

}