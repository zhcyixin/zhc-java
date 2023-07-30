package com.zhc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用数据结构返回定义
 * @author zhouhengchao
 * @since  2023-07-16 16:42:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;

    private String message;

    private T data;

    public Result(int code) {
        this.code = code;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(T data) {
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, null, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, null, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(500, null, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    public static <T> Result<T> error(Integer code,String message) {
        return new Result<>(code, message, null);
    }
}
