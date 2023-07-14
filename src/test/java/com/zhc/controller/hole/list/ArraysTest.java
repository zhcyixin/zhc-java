package com.zhc.controller.hole.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java8 引入Stream API之后对List操作很方便，因此通常会存在将数组转为List的场景；
 * 然而很多时候Arrays.asList把数组转换为List,实际使用中存在一些坑
 * @author zhouhengchao
 * @since 2023-07-13 17:37:00
 */

@Slf4j
public class ArraysTest {

    /**
     * 定义一个int类型数组，调用Arrays.asList方法将数组转为List
     *
     * 结果：通过日志看到，结果不是预期的有3个数字的list，得到的List包含的其实是一个 int 数组，整个List元素个数是1，元素类型是整数数组
     *
     * 原因分析：通过查看源码 public static <T> List<T> asList(T... a)，接收的是泛型参数，
     * 可以把int装箱为Integer，不可能把int数组装箱为Integer数组，结果把int数组整体作为了一个对象成为了泛型类型 T
     *
     * 结论：不能直接使用 Arrays.asList 来转换基本类型数组
     */
    @Test
    void testIntArrayToList1(){
        int[] arr = {66, 88, 99};
        List list = Arrays.asList(arr);
        log.info("list:{} size:{} data:{}", list, list.size(), list.get(0));
    }

    /**
     * 因此得出方案，可以定义包装类型的数组；
     * 或者也可以通过Arrays.stream方法转换，如果是Jdk8及以上版本
     */
    @Test
    void testIntArrayToList2(){
        Integer[] arr1 = {66, 88, 99};
        List list1 = Arrays.asList(arr1);
        log.info("list1:{} size:{} data:{}", list1, list1.size(), list1.get(0));

        int[] arr2 = {66, 88, 99};
        List list2 = Arrays.stream(arr2).boxed().collect(Collectors.toList());
        log.info("list2:{} size:{} data:{}", list2, list2.size(), list2.get(0));
    }

    /**
     * 通过Arrays.asList生成一个list，然后调用list的add方法新增元素
     * 结果：调用add方法时报错，java.lang.UnsupportedOperationException
     * 原因分析：通过查看源码发现Arrays.asList 返回的 List 并不是我们期望的 java.util.ArrayList，而是 Arrays 的内部类 ArrayList。
     * ArrayList 内部类继承自 AbstractList 类，并没有覆写父类的 add 方法
     *
     * 结论：Arrays.asList 返回的 List 不支持增删操作，
     */
    @Test
    void testArrayToListAdd1(){
        Integer[] arr = {66, 88, 99};
        List list = Arrays.asList(arr);
        list.add(100);
        log.info("list1:{} size:{} data:{}", list, list.size(), list.get(3));
    }

    /**
     * 解决方案：重新new 一个ArrayList
     */
    @Test
    void testArrayToListAdd2(){
        Integer[] arr = {66, 88, 99};
        List<Integer> list = new ArrayList<>(Arrays.asList(arr));
        list.add(100);
        log.info("list1:{} size:{} data:{}", list, list.size(), list.get(3));
    }

    /**
     * 结果：对原始数组的修改会影响到我们获得的那个 List
     * 原因分析：通过看源码发现，ArrayList 其实是直接使用了原始的数组，导致数据被共享，产生问题
     * 解决方案：重新new一个数组，运行发现后面对数组的修改不再影响到List的使用
     */
    @Test
    void testArrayToListUpdate(){
        Integer[] arr = {66, 88, 99};
        List list = Arrays.asList(arr);
        arr[1] = 55;
        log.info("list:{} size:{} arr:{}", list, list.size(), arr);

        List list1 = new ArrayList(Arrays.asList(arr));
        arr[1] = 22;
        log.info("list1:{} size:{} arr:{}", list1, list1.size(), arr);
    }
}
