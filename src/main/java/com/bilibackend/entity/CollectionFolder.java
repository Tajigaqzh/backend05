package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bilibackend.validate.AddGroup;
import com.bilibackend.validate.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @TableName bili_collection_folder
 */
@TableName(value = "bili_collection_folder")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionFolder implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    /**
     *
     */
    @TableField("name")
    @NotNull(groups = AddGroup.class)
    private String name;

    /**
     *
     */
    @TableField("userId")
    @NotNull(groups = AddGroup.class)
    private Long userId;

    /**
     *
     */
    @TableField("description")
    private String description;

}