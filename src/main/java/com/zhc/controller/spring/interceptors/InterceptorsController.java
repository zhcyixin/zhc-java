package com.zhc.controller.spring.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 场景案例：通过WebMvcConfigurer接口 + HandlerInterceptor自定义拦截器 +自定义注解实现用户请求登录态校验；
 *
 *
 * @author zhouhengchao
 * @since 2023-10-08 15:25:00
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/spring/intercept")
public class InterceptorsController {

    /**
     * 获取用户信息方法 - 该方法需要限制用户必须登录才可以访问
     * 添加@Authentication注解，表示该接口方法需要校验登录态
     * @return
     */
    @Authentication
    @GetMapping("/getUserInfo")
    public String getUserInfo(){
        log.info("获取用户信息成功。");
        return "用户名:hello_world";
    }

    /**
     * 获取地区数据方法 - 该方法用户未登录时也可以访问
     * 未添加注解@Authentication注解，不需要校验登录态
     * @return
     */
    @GetMapping("/getAreaData")
    public String getAreaData(){
        log.info("获取地区成功。");
        return "获取地区成功";
    }

}
