package com.zhc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1、自定义注解-控制接口限流
 * 2、在需要限流的方法添加该注解即可
 * 3、该注解可以放在方法上
 * @author zhouhengchao
 * @since 2023-06-13 17:30:00
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limiter {

    // 默认每秒放入桶中的令牌数
    double limitNum() default 5;

    // 限流器名称，使用该注解的每一个方法上，保证全局唯一性
    String name() default "";
}
