package com.zhc.demo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 1、日常开发中，可能大家会遇到一些两个集合间的一些操作，例如求两集合相同元素(交集)、两集合不同元素(差集)等等；
 * 2、下面结合具体的代码示例，给大家介绍一下，集合间的交集、并集、差集、补集的计算
 * 3、通过几种方法进行对比，希望对大家有所帮助。
 * @author zhouhengchao
 * @since 2023-06-19 10:35:00
 * @version 1.0
 */
public class CollectionOperationTest {

    // 定义两个集合list1、list2
    private ArrayList<Integer> list1 = Lists.newArrayList(1,2,3,4);
    private ArrayList<Integer> list2 = Lists.newArrayList(3,4,5,6);
    /**
     * 集合交集： 取2个集合中，相同的部分 （list1 交集 list2，取 list1和list2 中相同的元素）
     * 两个集合的交集计算：
     */
    @Test
    void testIntersectionCollection(){
        /**
         * 1、传统方法，通过两个for循环进行遍历比较,定义一个新的集合接收
         */
        ArrayList<Object> destList = Lists.newArrayList();
        for (Integer e1 : list1) {
            for (Integer e2 : list2) {
                if(e1.equals(e2)){
                    destList.add(e1);
                }
            }
        }
        System.out.println("intersection 交集结果是： " + destList);

        /**
         * 2、通过ArrayList下的retainAll方法,如果list1和list2有相同元素，list1只保留相同的，去掉不同的；
         * 如果没有相同元素，list1变为空
         * retainAll 方法的返回值，在list1集合有变化就返回true，没变化返回false
         */
        boolean res = list1.retainAll(list2);
        System.out.println("intersection retainAll 方法 交集结果是： " + list1);
        System.out.println("intersection retainAll 方法 交集返回值是： "+ res);

        /**
         * 3、也可以使用stream api，通过filter和list的contains方法
         */
        List<Integer> result = list1.stream().filter(o->list2.contains(o)).collect(Collectors.toList());
        System.out.println("intersection retainAll 方法 交集返回值是： "+ result);

        /**
         * 4、通过org.apache.commons.collections4.CollectionUtils工具类的intersection方法
         */
        Collection<Integer> intersection = CollectionUtils.intersection(list1, list2);
        System.out.println("intersection CollectionUtils.intersection 方法 交集返回值是： "+ intersection);
    }

    /**
     * 集合交集：将2个集合，合并为一个集合中
     * 两个集合的并集计算：
     */
    @Test
    void testUnionCollection(){
        /**
         * 1、传统方法，通过两次for循环进行遍历,定义一个新的集合接收
         */
        ArrayList<Object> destList = Lists.newArrayList();
        for (Integer e1 : list1) {
            if(!destList.contains(e1)){
                destList.add(e1);
            }
        }
        for (Integer e2 : list2) {
            if(!destList.contains(e2)){
                destList.add(e2);
            }
        }
        System.out.println("传统方法 并集结果是： " + destList);

        /**
         * 2、通过HashSet来进行去重处理，分别将list1、list2都放到set中
         * 比第一种简便
         */
        HashSet<Integer> set = Sets.newHashSet();
        set.addAll(list1);
        set.addAll(list2);
        System.out.println("通过HashSet方法 并集结果是： " + set);

        /**
         * 3、通过org.apache.commons.collections4.CollectionUtils工具类的union方法
         * 推荐使用
         */
        Collection<Integer> result = CollectionUtils.union(list1, list2);
        System.out.println("CollectionUtils.union 方法 并集是： "+ result);

    }

    /**
     * 集合差集：取2个集合中，不相同的部分 （list1 差集 list2 ，取 list1中与list2 不相同的元素）
     * 两个集合的差集计算：
     */
    @Test
    void testSubtractCollection(){
        // 传统方法通过for循环遍历处理

        /**
         * 1、通过ArrayList的removeAll方法
         * 比第一种简便
         */
        boolean b = list1.removeAll(list2);
        System.out.println("通过removeAll方法 差集结果是： " + list1);

        /**
         * 2、通过org.apache.commons.collections4.CollectionUtils工具类的subtract方法
         * 推荐使用
         */
        Collection<Integer> result = CollectionUtils.subtract(list1, list2);
        System.out.println("CollectionUtils.subtract 方法 并集是： "+ result);
    }

    /**
     * 集合补集：取2个集合中，不相同的部分 ，组成新集合 （ list1 差集 list2 并 list2 差集 list1)
     * 两个集合的补集计算：
     */
    @Test
    void testSuppleCollection(){
        /**
         * 通过四次for循环遍历处理，
         * 代码比较复杂麻烦
         */
        ArrayList<Integer> destList = Lists.newArrayList();
        boolean flag = false;
        for (Integer e1 : list1) {
            for (Integer e2 : list2) {
                if(e1.equals(e2)){
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                destList.add(e1);
            } else {
                flag = false;
            }
        }

        // list1 和 list2 换位置对比下
        flag = false;
        for (Integer e1 : list2) {
            for (Integer e2 : list1) {
                if(e1.equals(e2)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                destList.add(e1);
            }else {
                flag = false;
            }
        }
        System.out.println("通过循环比较 补集：" +destList);

        /**
         * 2、通过将list转为map，然后使用map进行对比
         * 比第一种稍微简单一些，还是比较复杂
         */
        Map<Integer, Integer> map1 = list1.stream().collect(Collectors.toMap(k -> k, v -> v));
        Map<Integer, Integer> map2 = list2.stream().collect(Collectors.toMap(k -> k, v -> v));

        map1.forEach((k,v)->{
            Integer val = map2.get(k);
            if(null == val){
                destList.add(k);
            }
        });

        map2.entrySet().stream().forEach(e->{
            Integer key = e.getKey();
            Integer val = map1.get(key);
            if(null == val){
                destList.add(key);
            }
        });
        System.out.println("disjunctionTest2 补集：" +destList);

        /**
         * 3、使用 list1 和 list2相互差集，再并集
         */
        Collection<Integer> s1 = CollectionUtils.subtract(list1, list2);
        Collection<Integer> s2 = CollectionUtils.subtract(list2, list1);
        Collection<Integer> union = CollectionUtils.union(s1, s2);
        System.out.println("通过2个差集再并集计算结果为：" +union);

        /**
         * 4、通过org.apache.commons.collections4.CollectionUtils工具类的disjunction 方法
         * 只需一行代码，推荐使用
         */
        Collection<Integer> result = CollectionUtils.disjunction(list1, list2);
        System.out.println("CollectionUtils.disjunction 方法 差集是： "+ result);
    }
    /**
     * 总结一下：
     * 1、Java中对List集合的常见处理，可以手动通过循环实现，也可以通过通过调用工具类，简单实现；
     * 2、工作中多积累总结一些实用的api，提高编码效率，可以有更多时间去做其他事情；
     * 若视频中内容对您有帮助，请三连加关注支持，后续会定期更新分享工作中的经验和技术类干货。
     */

}
