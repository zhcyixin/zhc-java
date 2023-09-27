package com.zhc.controller.spring.annotation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 相信很多小伙伴在工作中都有使用过自定义注解，注解就是一种标志，单独使用注解，就相当于在类、方法、参数和包上加上一个装饰，什么功能也没有，
 * 仅仅是一个标志，然后这个标志可以加上一些自己定义的参数，可以通过AOP、拦截器对包含注解的类、方法、参数进行特殊处理，注解就有了实际意义。
 *
 * 通过使用自定义注解可以实现参数校验、日志记录、权限控制、异常处理、数据绑定等功能，使代码更加简洁易读，与业务代码进行解耦。
 * 今天分享的内容就是如何使用自定义注解进行参数校验，
 *
 * @author zhouhengchao
 * @since 2023-09-25 14:47:00
 * @version 1.0
 */

@RestController
@RequestMapping("/v1/zhc/java/spring/annotation")
public class AnnotationController {


}
