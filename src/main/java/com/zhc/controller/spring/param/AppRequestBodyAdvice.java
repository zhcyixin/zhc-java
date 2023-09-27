package com.zhc.controller.spring.param;

import cn.hutool.core.codec.Base64;
import com.zhc.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author zhouhengchao
 * @since 2023-09-26 16:36:00
 */
@Slf4j
@ControllerAdvice
public class AppRequestBodyAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        //只在dev环境有效,实际中可以根据情况修改
        if (!"dev".equals(SpringUtils.getActiveProfile())) {
            return false;
        }
        Class<?> parameterType = methodParameter.getParameterType();
        return Arrays.stream(parameterType.getDeclaredFields()).anyMatch(filed -> Objects.nonNull(filed.getAnnotation(ConvertPathToBase64.class)));
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                           Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        for (Field field : o.getClass().getDeclaredFields()) {
            ConvertPathToBase64 annotation = field.getAnnotation(ConvertPathToBase64.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(o);
                if (value instanceof String) {
                    field.set(o, Base64.encode(value.toString()));

                }
            } catch (IllegalAccessException e) {
                log.error("Image url parse error", e);
            }
        }
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                  Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
