package com.zhc.controller.hole.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Configuration
@RestController
@RequestMapping("/v1/zhc/java/hole/asyncHole/fanoutright")
public class FanoutQueueRight {
    private static final String MEMBER_QUEUE = "newusermember";
    private static final String PROMOTION_QUEUE = "newuserpromotion";
    private static final String EXCHANGE = "newuser";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping
    public void sendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE, "", UUID.randomUUID().toString());
    }
    @Bean("declarables2")
    public Declarables declarables() {
        //会员服务队列
        Queue memberQueue = new Queue(MEMBER_QUEUE);
        //营销服务队列
        Queue promotionQueue = new Queue(PROMOTION_QUEUE);
        //广播交换器
        FanoutExchange exchange = new FanoutExchange(EXCHANGE);
        //两个队列绑定到同一个交换器
        return new Declarables(memberQueue, promotionQueue, exchange,
                BindingBuilder.bind(memberQueue).to(exchange),
                BindingBuilder.bind(promotionQueue).to(exchange));
    }
    @RabbitListener(queues = MEMBER_QUEUE)
    public void memberService1(String userName) {
        log.info("memberService1: welcome message sent to new user {}", userName);
    }
    @RabbitListener(queues = MEMBER_QUEUE)
    public void memberService2(String userName) {
        log.info("memberService2: welcome message sent to new user {}", userName);
    }
    @RabbitListener(queues = PROMOTION_QUEUE)
    public void promotionService1(String userName) {
        log.info("promotionService1: gift sent to new user {}", userName);
    }
    @RabbitListener(queues = PROMOTION_QUEUE)
    public void promotionService2(String userName) {
        log.info("promotionService2: gift sent to new user {}", userName);
    }
}
