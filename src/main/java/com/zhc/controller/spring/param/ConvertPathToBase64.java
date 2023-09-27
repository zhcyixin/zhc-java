package com.zhc.controller.spring.param;

import java.lang.annotation.*;

/**
 * 通过自定义参数实现请求参数转换
 *
 * @author zhouhengchao
 * @since 2023-09-26 11:39:00
 */

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertPathToBase64 {
}
