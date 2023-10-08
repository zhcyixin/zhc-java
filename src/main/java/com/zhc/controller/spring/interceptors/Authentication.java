package com.zhc.controller.spring.interceptors;

import java.lang.annotation.*;

/**
 * 自定义注解-用户登录态校验
 *
 * @author zhouhengchao
 * @since 2023-10-08 11:39:00
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authentication {
}
