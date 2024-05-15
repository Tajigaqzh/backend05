package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 19:05
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "bili_sys_user_role")
@Schema(description = "中间表")
public class UserRole {
    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;
}
