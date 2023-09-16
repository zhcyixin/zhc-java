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

/**
 * 消息广播问题,对于发送的用户注册欢迎消息，我们设计了会员服务、还有营销服务，这两类服务都是多实例，
 * 需要保证两类服务都可以收到消息、但是每类服务只有一个实例能收到，如下采用了rabbitMq的广播交换器
 *
 * 运行后发现，两类服务的每个实例都收到了消息；
 *
 * 原因分析：广播交换器会忽略 routingKey，广播消息到所有绑定的队列，会员服务和营销服务都绑定了同一个队列，所以这四个服务只有一个能收到一次消息：
 *
 * 解决方案：把队列进行拆分，会员和营销两组服务分别使用一条独立队列绑定到广播交换器
 *
 * @author zhouhengchao
 * @since 2023-09-16
 *
 */
@Slf4j
@Configuration
@RestController
@RequestMapping("/v1/zhc/java/hole/asyncHole/fanoutwrong")
public class FanoutQueueWrong {
    private static final String QUEUE = "newuser";
    private static final String EXCHANGE = "newuser";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public void sendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE, "", UUID.randomUUID().toString());
    }
    //声明FanoutExchange，然后绑定到队列，FanoutExchange绑定队列的时候不需要routingKey
    @Bean("declarables1")
    public Declarables declarables() {
        Queue queue = new Queue(QUEUE);
        FanoutExchange exchange = new FanoutExchange(EXCHANGE);
        return new Declarables(queue, exchange,
                BindingBuilder.bind(queue).to(exchange));
    }
    //会员服务实例1
    @RabbitListener(queues = QUEUE)
    public void memberService1(String userName) {
        log.info("memberService1: welcome message sent to new user {}", userName);

    }
    //会员服务实例2
    @RabbitListener(queues = QUEUE)
    public void memberService2(String userName) {
        log.info("memberService2: welcome message sent to new user {}", userName);

    }
    //营销服务实例1
    @RabbitListener(queues = QUEUE)
    public void promotionService1(String userName) {
        log.info("promotionService1: gift sent to new user {}", userName);
    }
    //营销服务实例2
    @RabbitListener(queues = QUEUE)
    public void promotionService2(String userName) {
        log.info("promotionService2: gift sent to new user {}", userName);
    }
}
