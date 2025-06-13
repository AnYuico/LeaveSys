package com.g5.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;    // 状态码
    private String message;  // 提示信息
    private T data;          // 返回数据

    // 成功返回（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功返回（无数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 失败返回（自定义消息）
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // 自定义状态码和信息
    public static <T> Result<T> of(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
