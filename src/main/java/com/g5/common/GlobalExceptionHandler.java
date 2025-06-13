package com.g5.common;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.exception.NotRoleException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotRoleException.class)
    public Result<?> handleNotRoleException(NotRoleException e) {
        String role = e.getRole();
        return Result.error("权限不足，缺少角色: " + (role.equals("2") ? "教师" : "领导"));
    }

    // 你也可以处理其他 Sa-Token 异常，比如 NotLoginException 等
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e) {
        return Result.error("尚未登录");
    }
}