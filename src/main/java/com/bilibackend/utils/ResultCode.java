package com.bilibackend.utils;

public enum ResultCode {

    SUCCESS_CALLBACK(0, "ok"),
    ERROR_CALLBACK(1, "error"),
    SUCCESS(200, "成功"),

    NONE_TOKEN(401, "未登录，获取不到token"),

    NAME_PASS_ERROR(402, "用户名或密码错误"),
    EXPIRE(403, "无权限"),

    PARAM_ERROR(400, "参数错误"),

    QUERY_ERROR(404, "查询失败"),

    INSERT_ERROR(405, "新增失败"),

    UPDATE_ERROR(406, "更新失败"),

    DELETE_ERROR(407, "删除失败"),

    OTHER_ERROR(408, "其他错误"),

    TOKEN_ERROR(409, "非法token"),
    UN_SUPPORT_METHOD(410, "方法不支持"),

    ERROR(500, "服务器错误");

    private Integer code;
    private String data;

    private ResultCode() {
    }

    private ResultCode(Integer code, String data) {
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return data;
    }
}

