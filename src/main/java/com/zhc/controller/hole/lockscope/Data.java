package com.zhc.controller.hole.lockscope;

import lombok.Getter;

class Data {
    @Getter
    private static int counter = 0;
    private static Object locker = new Object();

    public static int reset() {
        counter = 0;
        return counter;
    }

    /**
     * 定义一个实例方法进行计数累加，
     * 考虑到存在线程安全问题，加上锁
     */
    public synchronized void wrong() {
        counter++;
    }

    /**
     * 定义一个静态变量加锁，并给该字段加上锁
     * 就加上了类级别的锁
     */
    public void right() {
        synchronized (locker) {
            counter++;
        }
    }
}
