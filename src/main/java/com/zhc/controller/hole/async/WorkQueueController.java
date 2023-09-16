package com.zhc.controller.hole.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Configuration
@RestController
@RequestMapping("/v1/zhc/java/hole/asyncHole/workqueue")
public class WorkQueueController {

    private static final String EXCHANGE = "newuserExchange";

    private static final String QUEUE = "newuserQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${server.port}")
    private Integer port;

    /**
     * 启动两个相同的服务，端口分别为8081、8082
     * 通过运行后发现两个服务控制台都收到了消息
     * 原因分析：1、RabbitMQ 的直接交换器根据 routingKey 对消息进行路由；
     * 2、程序每次启动都会创建匿名（随机命名）的队列，所以相当于每一个会员服务实例都对应独立的队列，以空 routingKey 绑定到直接交换器；
     * 3、服务发出消息的时候也设置了 routingKey 为空，所以直接交换器收到消息之后，发现有两条队列匹配，于是都转发了消息
     *
     * 解决方法：
     * 将队列改为指定的队列名称就能保证重复发送问题
     */
    @GetMapping
    public void sendMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE, "", UUID.randomUUID().toString());
    }

    //使用匿名队列作为消息队列
    @Bean()
    public Queue queue() {
//        return new AnonymousQueue();
        return new Queue(QUEUE);
    }

    //声明DirectExchange交换器，绑定队列到交换器
    @Bean
    public Declarables declarables() {
        DirectExchange exchange = new DirectExchange(EXCHANGE);
        return new Declarables(queue(), exchange,
                BindingBuilder.bind(queue()).to(exchange).with(""));
    }

    //监听队列，队列名称直接通过SpEL表达式引用Bean
    @RabbitListener(queues = "#{queue.name}")
    public void memberService(String userName) {
        log.info("memberService: welcome message sent to new user {} from {}", userName, port);

    }
}
