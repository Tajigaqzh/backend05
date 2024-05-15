package com.bilibackend.utils;

import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class Result {
    private Integer code;

    private Object data;

    private String message;


    public static Result result(ResultCode resultCode, Object data) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.data = data;
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        ResultCode resultCode = ResultCode.SUCCESS;
        result.code = resultCode.getCode();
        result.data = data;
        return result;
    }

    public static Result success() {
        return success("ok");
    }

    public static String okJSON(Object data) {
        return JSONUtil.toJsonStr(success(data));
    }

    public static String okJSON() {
        return JSONUtil.toJsonStr(ok());
    }

    public static Result ok(Object data) {
        return success(data);
    }

    public static Result ok() {
        return success(null);
    }


    public static Result error(ResultCode resultCode) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.data = null;
        result.message = resultCode.getMsg();
        return result;
    }


    public static Result error(ResultCode resultCode, Object data) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.data = data;
        return result;
    }

    public static Result error(ResultCode resultCode, Object data, String errorMessage) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.data = data;
        result.message = errorMessage;
        return result;
    }

    public static String errorJSON(ResultCode resultCode) {
        return JSONUtil.toJsonStr(error(resultCode));
    }

    public static String errorJSON(ResultCode resultCode, Object data) {
        return JSONUtil.toJsonStr(error(resultCode));
    }

    public static String errorJSON(ResultCode resultCode, Object data, String message) {
        return JSONUtil.toJsonStr(error(resultCode, data, message));
    }
}
