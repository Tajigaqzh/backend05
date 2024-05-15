package com.bilibackend.expection;

import cn.dev33.satoken.exception.NotLoginException;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author 20126
 * @Description 全局异常处理器
 * @Date 2024/5/11 14:42
 * @Version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 删除的时候捕获由事务抛出的错误
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({DeleteException.class})
    public String handleDeleteException(DeleteException exception) {
        log.warn(exception.getMessage());
        return Result.errorJSON(ResultCode.DELETE_ERROR);
    }

    @ExceptionHandler({NotLoginException.class})
    public String handleNotLoginException(NotLoginException exception) {


        // 打印堆栈，以供调试
//        exception.printStackTrace();

        log.warn(exception.getMessage());

        // 判断场景值，定制化异常信息 
//        String message = "";
        if (exception.getType().equals(NotLoginException.NOT_TOKEN)) {
            return Result.errorJSON(ResultCode.NONE_TOKEN);
        } else if (exception.getType().equals(NotLoginException.INVALID_TOKEN)) {
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
//            message = "token 无效";
        } else if (exception.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
//            message = "token 已过期";
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
        } else if (exception.getType().equals(NotLoginException.BE_REPLACED)) {
//            message = "token 已被顶下线";
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
        } else if (exception.getType().equals(NotLoginException.KICK_OUT)) {
//            message = "token 已被踢下线";
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
        } else if (exception.getType().equals(NotLoginException.TOKEN_FREEZE)) {
//            message = "token 已被冻结";
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
        } else if (exception.getType().equals(NotLoginException.NO_PREFIX)) {
//            message = "未按照指定前缀提交 token";
            return Result.errorJSON(ResultCode.TOKEN_ERROR);
        } else {
//            message = "当前会话未登录";
            return Result.errorJSON(ResultCode.NONE_TOKEN);
        }

    }
}