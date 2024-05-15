package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 15:10
 * @Version 1.0
 */
@Data
@TableName(value = "bili_sys_role")
@Schema(description = "角色")
public class Role implements Serializable {
    private Long id;
    private String roleName;
    private String roleTag;
    private int deleteStatus;

}
