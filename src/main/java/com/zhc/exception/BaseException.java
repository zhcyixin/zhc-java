package com.zhc.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础异常
 *
 * @author zhouhengchao
 * @since 2023-07-29 16:35:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;


    public BaseException() {
    }

    public BaseException(String module) {
    }

    public BaseException(String module, Integer code) {
        this.module = module;
        this.code = code;
    }

    public BaseException(String module, Integer code, String message) {
        this.module = module;
        this.code = code;
        this.message = message;
    }

    public BaseException(String module, Integer code, String message, Throwable e) {
        super(e);
        this.module = module;
        this.code = code;
        this.message = message;
    }
}
