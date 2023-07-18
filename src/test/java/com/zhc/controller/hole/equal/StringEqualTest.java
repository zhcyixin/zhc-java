package com.zhc.controller.hole.equal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 前面跟大家聊了Integer判等中的一些坑，今天继续跟大家分享一下String判等中踩过的坑，
 * 1、对于基本类型，直接使用==，比较两个值是否相等；
 * 2、对于包装类或者引用类型，需要使用equals判断内容是否相等，使用==比较的是两个对象在内存中的地址
 *
 * @author zhouhengchao
 * @since 2023-07-14 11:06:00
 */
@Slf4j
public class StringEqualTest {

    /**
     * 首先介绍下Java常量池技术：为了节省内存，也叫做字符串驻留或池化；
     * 1、当通过双引号形式创建字符串对象时，JVM会先判断这个字符串；
     * 2、如果字符串常量池中存在相同内容的字符串对象的引用，则将这个引用返回；
     * 3、否则，创建新的字符串对象，然后将这个引用放入字符串常量池，并返回该引用
     *
     * 结果：输出为true
     */
    @Test
    void testString01(){
        String a = "hello";
        String b = "hello";
        log.info("a == b ? {}", a == b);
    }

    /**
     * 原因分析:通过 new 创建的两个 String 是不同对象，==比较的是引用地址，引用应该不同
     * 结果输出:false
     */
    @Test
    void testString02(){
        String a = new String("hello");
        String b = new String("hello");
        log.info("a == b ? {}", a == b);
    }

    /**
     * 原因分析:String 提供的 intern 方法也会走常量池机制，因此两个对象的引用地址相同
     * 结果输出:true
     */
    @Test
    void testString03(){
        String a = new String("world").intern();
        String b = new String("world").intern();
        log.info("a == b ? {}", a == b);
    }

    /**
     * 分析:使用equals判断包装类的值是否相等是属于正确的用法
     * 结果输出:true
     */
    @Test
    void testString04(){
        String a = new String("666");
        String b = new String("666");
        log.info("a == b ? {}", a.equals(b));
    }


    /**
     * 总结：
     *
     * 1、对于String类使用双引号赋值或者intern方法，都会使用字符串常量池机制，使用时注意；
     *
     * 2、通过new 创建的字符串对象都是不同的对象，比较内容用equals方法，比较引用地址用==，都是不相同的；
     *
     * 3、虽然使用String的intern方法可以触发常量池，但是需要谨慎使用intern方法，可能造成性能问题，
     * 如果要用一定要注意控制驻留的字符串的数量，并观察常量表的各项指标值
     */

}
