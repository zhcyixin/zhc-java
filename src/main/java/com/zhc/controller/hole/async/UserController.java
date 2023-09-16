package com.zhc.controller.hole.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 相信很多小伙伴在工作中都有使用过异步，合理的使用异步可以提升接口性能；例如：一个简单的用户注册流程，在用户完成注册之后，需要像用户发送欢迎的消息，
 * 用户发送消息的场景，对时效性要求不高，如果和用户注册放一起同步处理，比较消耗性能，可以通过异步处理消息发送，这里通过rabbitMq消息队列来实现；
 * 然而虽然使用消息队列对性能有帮助，使用起来也很方便，但是却也很容易出错，需要考虑如下几个问题：
 * 1、异步流程可靠性问题，消息在发送、传输、处理环节都可能丢失，需要考虑补偿机制，或者说消息队列异常时主备双活运行；
 *
 * 2、消息重复接收问题；
 *
 * 3、消息广播中遇到的问题。
 *
 * @author zhouhengchao
 * @since 2023-09-14 20:19
 */

@RestController
@Slf4j
@RequestMapping("/v1/zhc/java/hole/asyncHole/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 场景介绍：简单实现一个用户注册，异步发送欢迎消息的业务；
     * 从控制台输出可以看到达到了消息补偿的效果，
     * 实际中生产环境业务更复杂还需要考虑：
     * 1、配置补偿的频次、每次处理数据量，以及补偿线程池配置合适的值，根据实际情况；
     * 2、补偿任务的高可用，例如使用类似XXLJOB框架；
     * 3、实际中补偿到最大的用户id值，需要数据库存储。
     */
    @GetMapping("register")
    public void register() {
        // 循环10次的调用用户注册方法；
        IntStream.rangeClosed(1, 10).forEach(i -> {
            User user = userService.register();
            // 保证50%的用户注册欢迎消息发送失败
            if (ThreadLocalRandom.current().nextInt(10) % 2 == 0) {
                rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.ROUTING_KEY, user);
                log.info("sent mq user {}", user.getId());
            }
        });
    }
}
