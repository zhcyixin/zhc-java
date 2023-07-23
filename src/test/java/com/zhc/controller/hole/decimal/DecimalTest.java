package com.zhc.controller.hole.decimal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * 相信大家都有使用过double、float等小数类型，使用时如果不注意，会遇到很多坑，造成精度丢失问题;
 * 特别是对于财务或者金融类项目，精度丢失会造成很大的经济损失，进而引发严重生产事故。
 *
 * @author zhouhengchao
 * @since 2023-07-17 20:57:00
 */
@Slf4j
public class DecimalTest {

    /**
     * 先通过运行几个简单的小数计算
     * 看看是否能够得到我们预期的结果，计算机对浮点数不能很精确的表示。
     */
    @Test
    void testDecimal1(){
        System.out.println(0.1+0.2);
        System.out.println(1.0-0.7);
        System.out.println(4.015*100);
        System.out.println(122.3/100);

        double amount1 = 2.15;
        double amount2 = 1.10;
        if (amount1 - amount2 == 1.05)
            System.out.println("OK");

    }

    /**
     * 可能很多小伙伴都听过，使用BigDecimal类型，在浮点数精确表达和运算的场景中。
     * 通过换成BigDecimal类型之后，发现计算结果还是不符合预期，只是计算精度提高了一些
     */
    @Test
    void testDecimal2(){
        System.out.println(new BigDecimal(0.1).add(new BigDecimal(0.2)));
        System.out.println(new BigDecimal(1.0).subtract(new BigDecimal(0.7)));
        System.out.println(new BigDecimal(4.015).multiply(new BigDecimal(100)));
        System.out.println(new BigDecimal(122.3).divide(new BigDecimal(100)));
    }

    /**
     * 浮点数运算注意：使用 BigDecimal 表示和计算浮点数，要使用字符串的构造方法来初始化 BigDecimal
     */
    @Test
    void testDecimal3(){
        System.out.println(new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println(new BigDecimal("1.0").subtract(new BigDecimal("0.7")));
        System.out.println(new BigDecimal("4.015").multiply(new BigDecimal("100")));
        System.out.println(new BigDecimal("122.3").divide(new BigDecimal("100")));
    }

    /**
     * 小伙伴可能会问，虽然使用BigDecimal的字符串构造函数可以保证精确计算，但是可能有时候只有Double类型的变量
     * 该如何处理呢，可以尝试通过调用Double.toString来解决，发现计算结果比之前的方式小数点后多出一个0,；
     * 原因分析：BigDecimal 有 scale 和 precision 的概念，scale 表示小数点右边的位数，而 precision 表示精度，也就是有效数字的长度；
     * new BigDecimal(Double.toString(100)) 得到的 BigDecimal 的 scale=1、precision=4；
     * 而 new BigDecimal(“100”) 得到的 BigDecimal 的 scale=0、precision=3。对于 BigDecimal 乘法操作，返回值的 scale 是两个数的 scale 相加
     */
    @Test
    void testDecimal4(){
        System.out.println(new BigDecimal("4.015").multiply(new BigDecimal("100")));
        System.out.println(new BigDecimal("4.015").multiply(new BigDecimal(Double.toString(100))));
    }

    /**
     * 通过下面的案例可以打印出对应的scale、precision,进一步验证上面的结论
     */
    @Test
    void testDecimal5(){
        BigDecimal bigDecimal1 = new BigDecimal("100");
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(100d));
        BigDecimal bigDecimal3 = new BigDecimal(String.valueOf(100));
        BigDecimal bigDecimal4 = BigDecimal.valueOf(100d);
        BigDecimal bigDecimal5 = new BigDecimal(Double.toString(100));

        print(bigDecimal1); //scale 0 precision 3 result 401.500
        print(bigDecimal2); //scale 1 precision 4 result 401.5000
        print(bigDecimal3); //scale 0 precision 3 result 401.500
        print(bigDecimal4); //scale 1 precision 4 result 401.5000
        print(bigDecimal5); //scale 1 precision 4 result 401.5000
    }

    private static void print(BigDecimal bigDecimal) {
        log.info("scale {} precision {} result {}", bigDecimal.scale(), bigDecimal.precision(),
                bigDecimal.multiply(new BigDecimal("4.015")));
    }

    /**
     * 使用String.format方法进行格式化，发现运行结果为6.4和6.3
     * 原因分析：因为double 和 float 的 6.35 其实相当于 6.350xxx 和 6.349xxx，
     * String.format 采用四舍五入的方式进行舍入，结果由精度问题和舍入方式共同导致的
     */
    @Test
    void testDecimal6(){
        double num1 = 6.35;
        float num2 = 6.35f;
        System.out.println(String.format("%.1f", num1));//四舍五入
        System.out.println(String.format("%.1f", num2));
    }

    /**
     * 推荐使用BigDecimal，通过设置对应的scale和舍入方式
     * 结果符合预期
     */
    @Test
    void testDecimal7(){
        BigDecimal num1 = new BigDecimal("6.35");
        BigDecimal num2 = num1.setScale(1, BigDecimal.ROUND_DOWN);//向下舍入
        System.out.println(num2);
        BigDecimal num3 = num1.setScale(1, BigDecimal.ROUND_HALF_UP);//四舍五入
        System.out.println(num3);
    }

    /**
     * BigDecimal数据的判等操作
     */
    @Test
    void testDecimal8(){
        // 查看BigDecimal的equals源码发现，会同时比较value和scale，因此返回为false
        System.out.println(new BigDecimal("1.0").equals(new BigDecimal("1")));

        // 如果只是希望比较对应value是否相等，可以使用compareTo 方法
        System.out.println(new BigDecimal("1.0").compareTo(new BigDecimal("1"))==0);
    }

    /**
     * 注意数据的溢出，Integer和Long都是有具体的数据范围的，考虑使用 Math 类的 addExact、subtractExact 等 xxExact 方法进行数值运算，
     * 这些方法可以在数值溢出时主动抛出异常；
     * 在数据量较大时也可以使用BigInteger来进行计算使用
     */
    @Test
    void testLongData(){
        long m = Long.MAX_VALUE;
        System.out.println(m+1);// 数据发生溢出，打印为负数
        try {
            long l = Long.MAX_VALUE;
            System.out.println(Math.addExact(l, 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 总结：
     * 1、要精确表示浮点数，使用 BigDecimal。使用 BigDecimal 的 Double 入参的构造方法同样存在精度丢失问题，
     * 应该使用 String 入参的构造方法或者 BigDecimal.valueOf 方法来初始化；
     * 2、浮点数做精确计算，参与计算的各种数值应该使用 BigDecimal，都要通过 BigDecimal 的方法进行；
     * 3、浮点数格式化，使用 String.format会进行四舍五入，可使用 DecimalFormat 来明确指定舍入方式，
     * 推荐使用BigDecimal表示浮点数，并使用setScale方法指定舍入的位数和方式；
     * 4、因为数据都是有范围的，要注意数据的溢出，虽然溢出不报错，但计算的结果都是错误的，推荐使用Math.xxxExact 方法来进行运算，
     * 在溢出时会抛出异常，大数据计算推荐使用BigInteger类
     */
}
