package com.zhc.controller.hole.decimal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 相信大家都有使用过double、float等小数类型，使用时如果不注意，会遇到很多坑，造成精度丢失问题。
 *
 * @author zhouhengchao
 * @since 2023-07-17 20:57:00
 */
@Slf4j
public class DecimalTest {

    /**
     * 先通过运行几个简单的小数计算
     * 看看是否能够得到我们预期的结果
     */
    @Test
    void testDecimal1(){
        System.out.println(0.1+0.2);
        System.out.println(1.0-0.8);
        System.out.println(4.015*100);
        System.out.println(123.3/100);

        double amount1 = 2.15;
        double amount2 = 1.10;
        if (amount1 - amount2 == 1.05)
            System.out.println("OK");

    }
}
