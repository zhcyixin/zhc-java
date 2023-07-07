package com.zhc.controller.hole.lockscope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Interesting {

    /**
     * volatile 关键字保证变量在多线程下的可见性
     * 可见性，是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道该变更，JMM规定了所有的变量都存储在主内存中；
     * 1、当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值立即刷新回主内存中；
     * 2、当读一个volatile变量时，JMM会把该线程对应的本地内存设置为无效，重新回到主内存中读取最新共享变量
     * 即写到主存中，从主存中读，保证了可见性
     */
    volatile int a = 1;
    volatile int b = 1;

    /**
     * 定义一个add方法将变量a、b各自增100万次
     */
    public void add() {
        log.info("add start");
        for (int i = 0; i < 1000000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    /**
     * 定义一个compare比较方法，当a<b时输出对应值
     */
    public void compare() {
        log.info("compare start");
        for (int i = 0; i < 1000000; i++) {
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
                // 最后的a>b应该始终是false的吗？
            }
        }
        log.info("compare done");
    }

    /**
     * 给add方法加上锁
     * 定义一个add方法将变量a、b各自增100万次
     */
    public synchronized void addRight() {
        log.info("add start");
        for (int i = 0; i < 1000000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    /**
     * 给compare方法也加上锁
     */
    public synchronized void compareRight() {
        log.info("compare start");
        for (int i = 0; i < 1000000; i++) {
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
            }
        }
        log.info("compare done");
    }
}
