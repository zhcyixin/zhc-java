package com.zhc.controller.spring.interceptors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 实现WebMvcConfigurer接口，添加拦截器注册
 *
 * @author zhouhengchao
 * @since 2023-10-08 10:31:37
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //获取用户信息接口拦截-/v1/zhc/java/spring/intercept 路径下会被拦截，使用tokenIntercepter拦截器
        registry.addInterceptor(tokenInterceptor())
                .addPathPatterns("/v1/zhc/java/spring/intercept/**")
                //获取地区列表数据 允许未登录访问
                .excludePathPatterns("/v1/zhc/java/spring/intercept/getAreaData");
    }

    /**
     * 处理请求跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3 * 24 * 60 * 60);
    }

    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor();
    }

}
