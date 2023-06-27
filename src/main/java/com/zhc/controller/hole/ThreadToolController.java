package com.zhc.controller.hole;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 线程重用：
 * 1、线程池的核心是线程重用，线程重用能够有效的减小线程频繁创建，减小时间消耗，便于线程管理；
 * 2、SpringBoot项目程序运行在 Tomcat 中，执行程序的线程是 Tomcat 的工作线程，而 Tomcat 的工作线程是基于线程池的。
 * @author zhouhengchao
 * @since 2023-06-27 14:12:00
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/hole/threadTool")
public class ThreadToolController {

    private static final ThreadLocal<Integer> currentStudent = ThreadLocal.withInitial(() -> null);

    /**
     * 需求描述：为每一个学生缓存学号信息，在线程间隔离变量，在方法或类间共享，使用ThreadLocal进行缓存
     * 缓存前先获取上一次的值，然后按照传入的学号信息设值，并一起打印出来;
     * 为了复现出问题，将tomcat线程池最大线程数配置为1，server.tomcat.threads.max = 1，保证两次都是请求的同一个线程
     * @param stuNo
     * @return
     */
    @GetMapping("/threadLocal/wrong")
    public Map wrong(@RequestParam("stuNo") Integer stuNo) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before = Thread.currentThread().getName() + ":" + currentStudent.get();
        //设置用户信息到ThreadLocal
        currentStudent.set(stuNo);
        // 设置用户信息之后再查询一次ThreadLocal中的用户信息
        String after = Thread.currentThread().getName() + ":" + currentStudent.get();
        //汇总输出两次查询结果
        Map result = new HashMap(); result.put("before", before);
        result.put("after", after);
        return result;
    }

    /**
     * 问题分析：1、在 Tomcat 这种 Web 服务器下跑的业务代码，本来就运行在一个多线程环境中，因为线程的创建比较昂贵，
     * 所以 Web 服务器往往会使用线程池来处理请求，这就意味着线程会被重用；
     * 2、使用类似 ThreadLocal 工具来存放一些数据时，需要特别注意在代码运行完后，显式地去清空设置的数据，使用了自定义的线程池，也同样会遇到这个问题。
     * @param stuNo
     * @return
     */
    @GetMapping("/threadLocal/right")
    public Map right(@RequestParam("stuNo") Integer stuNo) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before = Thread.currentThread().getName() + ":" + currentStudent.get();
        //设置用户信息到ThreadLocal
        currentStudent.set(stuNo);
        try {
            // 设置用户信息之后再查询一次ThreadLocal中的用户信息
            String after = Thread.currentThread().getName() + ":" + currentStudent.get();
            //汇总输出两次查询结果
            Map result = new HashMap(); result.put("before", before);
            result.put("after", after);
            return result;
        }finally{
            // 在finally中调用remove方法，显式清除 ThreadLocal 中的数据。这样一来，新的请求过来即使使用了之前的线程也不会获取到错误的用户信息了
            currentStudent.remove();
        }
    }

    //线程个数
    private static int THREAD_COUNT = 10;
    //总元素数量
    private static int ITEM_COUNT = 1000;

    //帮助方法，用来获得一个指定元素数量模拟数据的ConcurrentHashMap
    private ConcurrentHashMap<String, Long> getData(int count) {
        return LongStream.rangeClosed(1, count)
                .boxed()
                .collect(Collectors.toConcurrentMap(i -> UUID.randomUUID().toString(), Function.identity(),
                        (o1, o2) -> o1, ConcurrentHashMap::new));
    }

    /**
     * 1、使用了 ConcurrentHashMap，不代表对它的多个操作之间的状态是一致的，是没有其他线程在操作它的，如果需要确保需要手动加锁;
     * 2、诸如 size、isEmpty 和 containsValue 等聚合方法，在并发情况下可能会反映 ConcurrentHashMap 的中间状态。
     * 因此在并发情况下，这些方法的返回值只能用作参考，而不能用于流程控制；
     * 3、诸如 putAll 这样的聚合方法也不能确保原子性，在 putAll 的过程中去获取数据可能会获取到部分数据。
     * @return
     * @throws InterruptedException
     */
    @GetMapping("currentHashMap/wrong")
    public String currentHashMapWrong() throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        //初始900个元素
        log.info("init size:{}", concurrentHashMap.size());

        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        //使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            //查询还需要补充多少个元素
            int gap = ITEM_COUNT - concurrentHashMap.size();
            log.info("gap size:{}", gap);
            //补充元素
            concurrentHashMap.putAll(getData(gap));
        }));
        //等待所有任务完成
        forkJoinPool.shutdown();
        //阻塞当前线程直到 ForkJoinPool 中所有的任务都执行结束
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //最后元素个数会是1000吗？
        log.info("finish size:{}", concurrentHashMap.size());
        return "OK";
    }

    /**
     * 解决方案：将对应业务逻辑代码加锁即可
     * @return
     * @throws InterruptedException
     */
    @GetMapping("currentHashMap/right")
    public String currentHashMapRight() throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        //初始900个元素
        log.info("init size:{}", concurrentHashMap.size());

        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        //使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            synchronized (this){
                //查询还需要补充多少个元素
                int gap = ITEM_COUNT - concurrentHashMap.size();
                log.info("gap size:{}", gap);
                //补充元素
                concurrentHashMap.putAll(getData(gap));
            }
        }));
        //等待所有任务完成
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //最后元素个数会是1000吗？
        log.info("finish size:{}", concurrentHashMap.size());
        return "OK";
    }
}
