package com.zhc.controller.hole.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 消费者端逻辑，接收rabbitMq消息，打印用户注册欢迎消息
 *
 * @author zhouhengchao
 * @since 2023-09-14 20:53:00
 */
@Component
@Slf4j
public class MemberService {
    private Map<Long, Boolean> welcomeStatus = new ConcurrentHashMap<>();

    //监听用户注册成功的消息，发送欢迎消息
    @RabbitListener(queues = RabbitConfiguration.QUEUE)
    public void listen(User user) {
        log.info("receive mq user {}", user.getId());
        welcome(user);
    }

    /**
     * 发送欢迎消息
     * @param user
     */
    public void welcome(User user) {
        //去重操作
        if (welcomeStatus.putIfAbsent(user.getId(), true) == null) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
            }
            log.info("memberService: welcome new user {}", user.getId());
        }
    }
}
