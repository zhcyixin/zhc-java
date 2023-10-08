package com.zhc.controller.spring.interceptors;

import com.zhc.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhouhengchao
 * @since 2023-10-08 16:22:00
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("开始进入系统权限拦截器");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Authentication methodAnnotation = handlerMethod.getMethodAnnotation(Authentication.class);
        Authentication classAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Authentication.class);
        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }

        String tokenHeader = request.getHeader("x-auth-token");
        //  如果请求头中没有Authorization信息则直接禁用了
        if (tokenHeader == null || !tokenHeader.startsWith("token_")) {
            log.info("请求错误，token不存在:{}",tokenHeader);
            throw new BusinessException("登录失效，请重新登录");
        }
        if(tokenHeader.equals("token_test")){
            return true;
        }
        else {
            throw new BusinessException("非法请求");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
