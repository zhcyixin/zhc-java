package com.zhc.controller.hole.async;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定义一个用户实体类，包含id、name、registerTime三个字段
 * 通过@Data lombok注解编译时生成
 * @author zhouhengchao
 * @since 2023-09-14 20:46:00
 */
@Data
public class User implements Serializable {

    private static AtomicLong atomicLonng = new AtomicLong();

    /**
     * 用户id
     */
    private Long id = atomicLonng.incrementAndGet();

    /**
     * 用户名称
     */
    private String name = UUID.randomUUID().toString();

    /**
     * 注册时间
     */
    private LocalDateTime registerTime = LocalDateTime.now();
}
