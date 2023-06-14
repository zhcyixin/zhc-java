package com.zhc.aspect;

import com.zhc.core.annotation.Limiter;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1、Google的Guava工具包中就提供了一个限流工具类——RateLimiter，RateLimiter是基于“令牌通算法”来实现限流的；
 * 2、@Aspect 声明该类为一个切面类
 * @description: 限流注解-切面逻辑
 * @author zhouhengchao
 * @since  2022-04-14 10:59
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {
    // 定义一个并发环境的ConcurrentHashMap，用来存放不同方法的限流器
    private ConcurrentHashMap<String, RateLimiter> RATE_LIMITER = new ConcurrentHashMap<>();
    private RateLimiter rateLimiter;

    /**
     * 切入点，切面针对com.zhc.core.annotation.Limiter该注解进行功能增强
     */
    @Pointcut("@annotation(com.zhc.core.annotation.Limiter)")
    public void serviceLimit() {
    }

    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取拦截的方法名
        Signature sig = point.getSignature();
        //获取拦截的方法名
        MethodSignature msig = (MethodSignature) sig;
        //返回被织入增加处理目标对象
        Object target = point.getTarget();
        //为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        //获取注解信息
        Limiter annotation = currentMethod.getAnnotation(Limiter.class);
        //获取注解每秒加入桶中的token
        double limitNum = annotation.limitNum();
        // 注解所在方法名区分不同的限流策略
        String functionName = msig.getName();

        if (RATE_LIMITER.containsKey(functionName)) {
            rateLimiter = RATE_LIMITER.get(functionName);
        } else {
            RATE_LIMITER.put(functionName, RateLimiter.create(limitNum));
            rateLimiter = RATE_LIMITER.get(functionName);
        }
        if (rateLimiter.tryAcquire()) {
            log.info("流量控制策略之内");
            return point.proceed();
        } else {
            log.info("您被限流了。");
            throw new RuntimeException("手速太快啦，休息一下再试试。");
        }
    }
}
