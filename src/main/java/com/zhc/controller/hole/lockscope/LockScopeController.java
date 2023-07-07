package com.zhc.controller.hole.lockscope;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 *
 * 在并发编程中，相信很多小伙伴都有使用锁，小伙伴们掌握了锁的正确使用姿势了吗？
 * 1、通过简单有趣的加锁案例引入；
 * 2、聊聊实例级别锁和类级别锁；
 * 3、谈谈加锁的粒度和范围问题；
 * 4、常见加锁方法选择介绍，如什么时候使用乐观锁、悲观锁。
 *

 * @author zhouhengchao
 * @since 2023-07-06 14:12:00
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/hole/lockScope")
public class LockScopeController {

    /**
     * 一、先看一个简单有趣的案例
     * 习惯性思维：a 和 b 同样进行累加操作，应该始终相等，compare 中的第一次判断应该始终不会成立，不会输出任何日志
     * 应该不会存在a<b即打印a和b的值得情况，
     * 而且a>b的结果永远是false吗？
     * 先卖一个关子，看结果
     *
     * 执行代码后发现不但输出了日志，而且更诡异的是，compare 方法在判断 a< b 也成立的情况下还输出了 a>b 也成立
     * @return
     */
    @GetMapping("/wrong")
    public String wrong() {
        Interesting interesting = new Interesting();
        new Thread(() -> interesting.add()).start();
        new Thread(() -> interesting.compare()).start();
        return "OK";
    }

    /**
     * 有小伙伴可能觉得是线程安全问题，在操作两个字段 a 和 b，有线程安全问题；
     * 应该为 add 方法加上锁，确保 a 和 b 的 ++ 是原子性的，就不会错乱了，顺手为add方法加上了锁，即调用addRight()方法
     * @return
     */
    @GetMapping("/wrong1")
    public String wrong1() {
        Interesting interesting = new Interesting();
        new Thread(() -> interesting.addRight()).start();
        new Thread(() -> interesting.compare()).start();
        return "OK";
    }

    /**
     * 首先思考一下：为什么锁可以解决线程安全问题呢。因为只有一个线程可以拿到锁，所以加锁后的代码中的资源操作是线程安全的；
     * 1、这个案例中的 add 方法始终只有一个线程在操作，显然只为 add 方法加锁是没用的；
     * 2、两个线程是交错执行 add 和 compare 方法中的业务逻辑，而且这些业务逻辑不是原子性的：
     * 3、a++ 和 b++ 操作中可以穿插在 compare 方法的比较代码中；
     * 4、更需要注意的是，a<b 这种比较操作在字节码层面是加载 a、加载 b 和比较三步，代码虽然是一行但也不是原子性的
     *
     * 正确的做法应该是，为 add 和 compare 都加上方法锁，确保 add 方法执行时，compare 无法读取 a 和 b
     * 因此：使用锁解决问题之前一定要理清楚，我们要保护的是什么逻辑，多线程执行的情况又是怎样的，分析清线程、业务逻辑和锁三者之间的关系，别随意添加无效的方法锁外
     * @return
     */
    @GetMapping("right")
    public String right() {
        Interesting interesting = new Interesting();
        new Thread(() -> interesting.addRight()).start();
        new Thread(() -> interesting.compareRight()).start();
        return "OK";
    }

    /**
     * 二、再来聊聊实例级别的锁和类级别锁
     * 1、通过并行流来执行，并行流底层是基于forkJoin的，默认线程数是CPU核数减一，存在线程安全问题，就给方法上加锁；
     * 2、理想的结果是：最终计算结果为1000000
     * 实际结果为：不等于1000000
     * @param count
     * @return
     */
    @GetMapping("/wrong2")
    public int wrong2(@RequestParam(value = "count", defaultValue = "1000000") int count) {
        Data.reset();
        // 创建一个并行流，执行1百万次，通过7个线程；因为并行流的底层是基于forkJoin的，默认线程数是CPU核数减一
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().wrong());
        return Data.getCounter();
    }

    /**
     * 问题分析：
     * 1、在非静态的 wrong 方法上加锁，只能确保多个线程无法执行同一个实例的 wrong 方法，却不能保证不会执行不同实例的 wrong 方法；
     * 2、静态的 counter 在多个实例中共享，所以必然会出现线程安全问题。
     *
     * 理清思路之后得出两种解决方案：
     * 1、在类中定义一个 Object 类型的静态字段，在操作 counter 之前对这个字段加锁
     * 2、把 wrong 方法定义为静态，这个时候锁是类级别的
     * 这里我们采用方案1，运行结果为1000000符合预期
     *
     * @param count
     * @return
     */
    @GetMapping("right2")
    public int right2(@RequestParam(value = "count", defaultValue = "1000000") int count) {
        Data.reset();
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().right());
        return Data.getCounter();
    }

    /**
     * 三、再看看加锁的粒度和范围问题
     * 在方法上加 synchronized 关键字实现加锁确实简单，也因此我曾看到一些业务代码中几乎所有方法都加了 synchronized，但这种滥用 synchronized 的做法：
     * 1、没必要。通常情况下 60% 的业务代码是三层架构，数据经过无状态的 Controller、Service、Repository 流转到数据库，
     * 没必要使用 synchronized 来保护什么数据；
     * 2、可能会极大地降低性能。项目使用 Spring 框架时，默认情况下 Controller、Service、Repository 是单例的，
     * 加上 synchronized 会导致整个程序几乎就只能支持单线程，造成极大的性能问题
     *
     * 正确的做法：仅对必要的代码块甚至是需要保护的资源本身加锁
     */
    private List<Integer> data = new ArrayList<>();

    // 不涉及共享资源的慢方法，线程sleep2秒
    private void slow() {
        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 错误的加锁方法，对slow方法和list操作方法都加锁
     * 耗时2秒多
     * @return
     */
    @GetMapping("wrong3")
    public int wrong3() {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {
            //加锁粒度太粗了
            synchronized (this) {
                slow();
                data.add(i);
            }
        });
        log.info("took:{}", System.currentTimeMillis() - begin);
        return data.size();
    }

    /**
     * 正确的加锁方法，仅对存在线程安全的list操作加锁,
     * 耗时200多ms，性能明显提升很多
     * @return
     */
    @GetMapping("right3")
    public int right3() {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {
            slow();
            //只对List加锁
            synchronized (data) {
                data.add(i);
            }
        });
        log.info("took:{}", System.currentTimeMillis() - begin);
        return data.size();
    }

    /**
     * 四、如果精细化考虑了锁应用范围后，性能还无法满足需求的话，我们就要考虑另一个维度的粒度问题了；
     * 1、对于读写比例差异明显的场景，考虑使用 ReentrantReadWriteLock 细化区分读写锁，来提高性能，常用在缓存写入；
     * 2、如果你的 JDK 版本高于 1.8、共享资源的冲突概率也没那么大的话，考虑使用 StampedLock 的乐观读的特性，进一步提高性能；
     * 3、JDK 里 ReentrantLock 和 ReentrantReadWriteLock 都提供了公平锁的版本，在没有明确需求的情况下不要轻易开启公平锁特性，
     * 在任务很轻的情况下开启公平锁可能会让性能下降上百倍
     */


    /**
     * 总结：
     * 1、使用 synchronized 加锁虽然简单，但我们首先要弄清楚共享资源是类还是实例级别的、会被哪些线程操作，synchronized 关联的锁对象或方法又是什么范围的
     *
     * 2、加锁尽可能要考虑粒度和场景，锁保护的代码意味着无法进行多线程操作。对于 Web 类型的天然多线程项目，对方法进行大范围加锁会显著降级并发能力，
     * 要考虑尽可能地只为必要的代码块加锁，降低锁的粒度；
     * 而对于要求超高性能的业务，还要细化考虑锁的读写场景，以及悲观优先还是乐观优先，尽可能针对明确场景精细化加锁方案，
     * 可以在适当的场景下考虑使用 ReentrantReadWriteLock、StampedLock 等高级的锁工具类；
     *
     * 3、业务逻辑中有多把锁时要考虑死锁问题，通常的规避方案是，避免无限等待和循环等待
     */

}
