package com.bilibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bilibackend.validate.AllowValue;
import com.bilibackend.validate.PhoneNumber;
import com.bilibackend.validate.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

/**
 * 用户 实体类
 *
 * @author hp getUserInfo的时候获取
 * <p>
 * 先给所有权限，但是要传一个角色
 * @date 2024-05-05 13:47:14
 */
@Data
@TableName(value = "bili_sys_user", autoResultMap = true)
@Schema(description = "用户")
public class User implements Serializable {

    /**
     * 主键ID
     **/
    @Schema(description = "主键ID")
    @NotNull(groups = UpdateGroup.class)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     **/
    @Schema(description = "用户名", maxLength = 20, minLength = 3)
    @Length(min = 3, max = 20)
    @NotBlank(message = "用户名不能为空")
    @TableField(value = "username")
    private String username;


    /**
     * 删除状态：0、已删除 1、未删除
     **/
    @Schema(description = "删除状态：0、已删除 1、未删除")
    @TableField(value = "deleteStatus")
    private int deleteStatus;


    /**
     * 用户昵称
     **/
    @Schema(description = "用户昵称", maxLength = 20, minLength = 3)
    @Length(min = 3, max = 20)
    @NotBlank(message = "昵称不能为空")
    @TableField(value = "nickname")
    private String nickname;


    /**
     * 密码
     **/
    @Schema(description = "密码", minLength = 10, maxLength = 20)
    @Length(min = 10, max = 50)
    @TableField(value = "password")
    private String password;

    /**
     * 性别：0、女 1、男
     **/
    @Schema(description = "性别：0、女 1、男")
    @AllowValue(intValues = {0, 1}, message = "账户状态只能是0或者1")
    @TableField(value = "sex")
    private int sex;

    @TableField(value = "avatar")
    private String avatar;

    /**
     * 手机号,允许为空
     **/
    @PhoneNumber(allowNull = true)
    @Schema(description = "手机号")
    @TableField(value = "mobile")
    private String mobile;

    @TableField("level")
    private Integer level;

    //基于role的权限控制，简单一些
    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private List<Permission> perms;
}
