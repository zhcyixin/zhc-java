package com.zhc.controller.hole.date;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 在java8之前，我们处理日期都是使用Date、Calender 和 SimpleDateFormat类，声明时间戳、格式化日期时间、时间等;
 * 但是却存在一些问题，例如易用性、可读性差，而且还存在线程安全问题，Java8推出的新日期时间类 LocalDateTime、LocalDate等，
 * 每个类功能清晰明确，无需借助外部工具类，并且线程安全；但是由于一些新老项目兼容问题，可能很多项目还在使用Date、Calender类处理时间，
 * 推荐去看看我的Java8日期API使用的视频，
 * 今天就给大家聊聊Java8之前使用日期存在的问题和坑。
 *
 */

/**
 * @author zhouhengchao
 * @since 2023-08-23 13:49:00
 */
@Slf4j
public class DateHoleTest {

//    private static ThreadLocal<SimpleDateFormat> threadSafeSimpleDateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 有小伙伴这样初始化过时间吗？初始化一个2019年12月31日11:12:13，输出的时间是 3029 年 1 月 31 日 11 点 12 分 13 秒，不符合预期值
     */
    @Test
    void testDateHole1(){
        Date date = new Date(2019, 12, 31, 11, 12, 13);
        log.info("初始化之后日期为:{}",date);
    }

    /**
     * 问题分析：初始化赋值时，年必须是1900的差值，月必须从0开始到11结束，修改后代码如下，输出结果符合预期
     */
    @Test
    void testDateHole2(){
        Date date = new Date(2019 - 1900, 11, 31, 11, 12, 13);
        log.info("初始化之后日期为:{}",date);
    }

    /**
     * 上面的实现方式，没有考虑时区问题，因为不同的时区输出的时间值是不一样的，
     * 如果需要考虑时区设置，可以使用Calendar类，需要注意初始化时月份从0开始，11结束，月份可以使用Calendar.DECEMBER初始化，不易出错；
     * 运行后输出：Tue Dec 31 11:12:13 CST 2019，通过不同的时区输出不同时间，需要注意时区使用问题；
     * 然而输出形式为：Wed Jan 01 00:12:13 CST 2020  ,但是可能更习惯年 / 月 / 日 时: 分: 秒格式该如何处理呢？
     */
    @Test
    void testDateHole3(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 11, 31, 11, 12, 13);
        System.out.println(calendar.getTime());
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calendar2.set(2019, Calendar.DECEMBER, 31, 11, 12, 13);
        System.out.println(calendar2.getTime());

    }

    /**
     * 每到年底很多小伙伴可能会踩时间格式化的坑，使用SimpleDateFormat格式化，例如：初始化日期2019-12-29日，并格式化输出
     * 输出后发现年为2020年，提前跨年了，
     * 原因分析：使用大写YYYY 来初始化 SimpleDateFormat，week year，也就是所在的周属于哪一年，这里idea也提示了。
     * 实际中2019-12-29 所在的周属于2020年，必须使用小写的yyyy进行格式化可以解决该问题。
     */
    @Test
    void testDateHole4(){
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        System.out.println("defaultLocale:" + Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.DECEMBER, 29,0,0,0);
        SimpleDateFormat YYYY = new SimpleDateFormat("YYYY-MM-dd");
//        SimpleDateFormat YYYY = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("格式化: " + YYYY.format(calendar.getTime()));
        System.out.println("weekYear:" + calendar.getWeekYear());
        System.out.println("firstDayOfWeek:" + calendar.getFirstDayOfWeek());
        System.out.println("minimalDaysInFirstWeek:" + calendar.getMinimalDaysInFirstWeek());

    }

    /**
     * 使用一个 100 线程的线程池，循环 20 次把时间格式化任务提交到线程池处理，每个任务中又循环 10 次解析 2020-01-01 11:12:13 这样一个时间表示,
     * 运行后程序报错，而且输出的日期时间也不正确，因为SimpleDateFormat存在线程安全问题，分析源代码：
     * 1、SimpleDateFormat 继承了 DateFormat，DateFormat 有一个字段 Calendar；
     * 2、SimpleDateFormat 的 parse 方法调用 CalendarBuilder 的 establish 方法，来构建 Calendar；
     * 3、establish 方法内部先清空 Calendar 再构建 Calendar，整个操作没有加锁。
     * 解决方案：通过 ThreadLocal 来存放 SimpleDateFormat，修改后代码正常输出
     * @throws InterruptedException
     */
    @Test
    void testDateHole5() throws InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            //提交20个并发解析时间的任务到线程池，模拟并发环境
            threadPool.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    try {
                        System.out.println(simpleDateFormat.parse("2020-01-01 11:12:13"));
//                        System.out.println(threadSafeSimpleDateFormat.get().parse("2020-01-01 11:12:13"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }

    /**
     * 当需要解析的字符串和格式不匹配的时候，SimpleDateFormat 表现得很宽容，还是能得到结果，不会报错。比如，我们期望使用 yyyyMM 来解析 20160901 字符串
     * 输出结果为：Mon Jan 01 00:00:00 CST 2091，不符合预期，因为程序把0901当成了月份，就是75年与2016年加一起就是2091年
     */
    @Test
    void testDateHole6() throws ParseException {
        String dateString = "20160901";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        System.out.println("result:" + dateFormat.parse(dateString));
    }

    /**
     * 使用date计算日期的坑，获取当前时间后，进一步获取对应时间戳，然后加上30天的毫秒数，计算结果竟然早于当前时间
     * 原因分析：int发生了溢出，修复方式将30改为30L，就会当做long型来计算,可以看到idea也有提示
     */
    @Test
    void testDateHole7(){
        Date today = new Date();
        Date nextMonth = new Date(today.getTime() + 30 * 1000 * 60 * 60 * 24);
//        Date nextMonth = new Date(today.getTime() + 30L * 1000 * 60 * 60 * 24);
        System.out.println(today);
        System.out.println(nextMonth);
    }

    /**
     * 通过上面案例看到，直接使用Date进行日期类计算容易出错，推荐使用Calendar类或者Java8的日期api处理；
     * 还可以通过 with 方法进行快捷时间计算，TemporalAdjusters类提供的api
     */
    @Test
    void testDateHole8(){
        // 使用Calendar日期类实现
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 30);
        System.out.println(c.getTime());
        // 使用java8的api实现
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime.plusDays(30));

    }
}
