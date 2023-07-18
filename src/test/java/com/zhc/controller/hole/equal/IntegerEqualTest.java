package com.zhc.controller.hole.equal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 相信大家工作中都会使用到equals和==，判断两个变量是否相等，
 * 1、对于基本类型，直接使用==，比较两个值是否相等；
 * 2、对于包装类或者引用类型，需要使用equals判断内容是否相等，使用==比较的是两个对象在内存中的地址
 *
 * @author zhouhengchao
 * @since 2023-07-14 11:06:00
 */
@Slf4j
public class IntegerEqualTest {

    /**
     * 结果输出为:a==b ? true
     * 可能会想到因为a和b都是包装类，通过==比较的是对象内存地址应该不相等；
     *
     * 原因分析：1、Integer a = 116;会触发装箱，调用Integer.valueOf方法；
     * 2、通过查看源码Integer.valueOf转换对于[-128,127]范围的数存在缓存,因此a和b指向同一对象，返回true
     */
    @Test
    void testInteger01(){
        Integer.valueOf(116);
        Integer a = 116;
        Integer b = 116;
        log.info("a == b ? {}", a==b);
    }

    /**
     * 给a和b赋值一个[-128,127]范围之外的数，看看输出结果为false
     * 原因分析：查看源码发现，在调用Integer.valueOf装箱操作时，如果在[-128,127]范围之外的，会调用new Integer(128)返回新的对象地址
     * 所以最终输出结果为false,
     * 尝试设置jvm参数加上  -XX:AutoBoxCacheMax=800，试试输出结果，结论是true
     */
    @Test
    void testInteger02(){
//        Integer.valueOf(127);
        Integer a = 128;
        Integer b = 128;
        log.info("a == b ? {}", a==b);
    }

    /**
     * 因为a是通过缓存获取的对象，b是通过new创建的，New出来的 Integer 始终是不走缓存的新对象，
     * 所以最终输出结果永远为false
     */
    @Test
    void testInteger03(){
        Integer a = 66; //Integer.valueOf(127)
        Integer b = new Integer(66); //new instance
        log.info("a == b ? {}", a==b);
    }

    /**
     * 同理两个对象都是通过new创建，都是不从缓存获取的新对象，所以输出结果为false
     */
    @Test
    void testInteger04(){
        Integer a = new Integer(66);
        Integer b = new Integer(66);
        log.info("a == b ? {}", a==b);
    }

    /**
     * 结果：输出为true
     * 原因分析：1、Integer a = 160会触发装箱操作，a为包装类；
     * 2、b为int基本数据类型，在a和b比较时，a会由包装类拆箱为基本类型，比较的是具体的数值，因此输出为true
     */
    @Test
    void testInteger05(){
        Integer a = 160;
        int b = 160;
        log.info("a == b ? {}", a == b);
    }

    /**
     * 总结：
     *
     * 1、对于Integer包装类，判断内容是否相等时要使用equals进行比较；
     *
     * 2、使用== 对Integer包装类型数据进行判等时，出现问题比较隐晦不易发现，在[-128,127]区间范围时，会从缓存取数，
     * 只有在超出对应区间后才不相等，需要多加注意。
     */

}
