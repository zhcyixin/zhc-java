package com.zhc.controller.spring.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 通过RestTemplateBuilder配置RestTemplate属性参数，
 * 如果不配置将会使用Spring Boot默认配置
 * @author zhouhengchao
 * @since 2023-10-20 11:45:00
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder
                //设置连接超时时间
                .setConnectTimeout(Duration.ofSeconds(5000))
                //设置读取超时时间
                .setReadTimeout(Duration.ofSeconds(5000))
                //设置认证信息
                .basicAuthentication("username","password")
                //设置根路径
                .rootUri("http://localhost:8081/")
                //构建
                .build();
    }
}
