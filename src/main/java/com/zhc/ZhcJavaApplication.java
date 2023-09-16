package com.zhc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhouhengchao
 * @since 2023-06-10 20:18:00
 * @version 1.0
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.zhc.dao","com.zhc.controller.hole.transactional"})
@EnableScheduling
public class ZhcJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhcJavaApplication.class, args);
    }

}
