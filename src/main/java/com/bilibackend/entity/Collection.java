package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName bili_collection
 */
@TableName(value = "bili_collection")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Collection implements Serializable {

    @TableField("folderId")
    private Long folderId;


    @TableField("videoId")
    private Long videoId;

    @TableField("collectTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectTime;
}