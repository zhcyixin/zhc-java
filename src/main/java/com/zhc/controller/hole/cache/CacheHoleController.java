package com.zhc.controller.hole.cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 开篇引入：相信很多小伙伴在工作中为了提升性能，都有使用过缓存，使用缓存实际是以空间换时间的思想，
 * 缓存性能可以达到关系型数据库的几倍甚至几十倍，在互联网高并发场景使用很多，例如redis；
 *
 * 今天就给大家分享一下缓存中使用的坑，缓存用得好是锦上添花，用的不好就是雪上加霜，主要包含以下内容；
 *
 * 1、不要把redis当做数据库使用；
 * 2、聊一下缓存雪崩问题；
 * 3、聊聊缓存击穿问题，以及使用布隆过滤器解决缓存击穿问题；
 * 4、介绍下缓存同步策略以及各种策略存在的问题。
 *
 *
 * @author zhouhengchao
 * @since 2023-08-15 17:45:00
 */
@RestController
@RequestMapping("/v1/zhc/java/hole/cacheHole")
@Slf4j
public class CacheHoleController {

    /**
     * 1、不要把redis当做数据库来使用，原因如下：
     * （1）Redis 中数据丢失导致业务问题，而且因为没有保留原始数据，业务都无法恢复，进而出现生产事故；
     * （2）不要太相信缓存不会出问题崩掉，系统设计必须保证在缓存不能使用时，也可以正常运行；
     * （3）redis中不能保存超过内存大小的数据，存储能力有限；
     * （4）redis服务端需要配置maxmemory 参数，保证redis性能，客户端对缓存数据要存储在数据库中。
     */

    /**
     * 介绍redis中key过期的策略
     * allkeys-lru:在所有key中，移除最近最少使用的key；
     * volatile-lru：针对带有过期时间的 Key，优先删除最近最少使用的 Key；
     * volatile-ttl：针对带有过期时间的 Key，优先删除即将过期的 Key（根据 TTL 的值）；
     * allkeys-lfu，针对所有Key，优先删除最少使用的 Key(Redis 4.0 以上);
     * volatile-lfu，针对带有过期时间的 Key，优先删除最少使用的 Key（Redis 4.0 以上）。
     *
     * 概括：简单的说就是按照key范围范围allkeys和volatile，算法策略包括LRU、LFU，
     * 实际中需要结合具体场景选择过期策略，例如服务启动初始化的key，永不过期，但是过期策略采用allKeys范围就会业务出错
     */

    /**
     * 2、缓存雪崩问题：短时内大量缓存失效的情况，由于缓存数据使用频繁IOPS较高，就会出现大量回溯数据库操作，
     * 大量请求打到数据库层，增加数据库压力，进而导致应用崩溃；即缓存失效也叫缓存雪崩。
     *
     * 原因分析：（1）缓存服务出现问题down掉，导致大量请求回溯到数据库，考虑缓存架构高可用以及恢复缓存服务；
     * （2）大量缓存key同时过期，导致大量请求回溯到数据库
     */
    @Resource
    private RedissonClient redissonClient;
    // 通过定义一个原子类变量，记录查询数据库次数
    private AtomicInteger atomicInteger = new AtomicInteger();

    @PostConstruct
    public void wrongInit() {
        //初始化1000个地区数据到Redis，所有缓存数据有效期30秒
        IntStream.rangeClosed(1, 1000).forEach(i ->
                redissonClient.getBucket("area" + i).set(getCityFromDb(i),30, TimeUnit.SECONDS));
        log.info("缓存数据初始化完成");
        //每秒一次，输出数据库访问的QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("数据库 QPS为 : {}", atomicInteger.getAndSet(0));
            }, 0, 1, TimeUnit.SECONDS);
    }

//    @PostConstruct
    public void rightInit1() {
        //这次缓存的过期时间是30秒+10秒内的随机延迟
        IntStream.rangeClosed(1, 1000).forEach(i -> redissonClient.getBucket("area" + i)
                .set(getCityFromDb(i), 30 + ThreadLocalRandom.current().nextInt(10), TimeUnit.SECONDS));
        log.info("缓存数据初始化完成");
        //同样1秒一次输出数据库QPS，通过固定时间间隔线程池输出数据库qbs
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("数据库 QPS为 : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);
    }

//    @PostConstruct
    public void rightInit2() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //每隔30秒全量更新一次缓存
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            IntStream.rangeClosed(1, 1000).forEach(i -> {
                String data = getCityFromDb(i);
                //模拟更新缓存需要一定的时间
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) { }
                if (!StringUtils.isEmpty(data)) {
                    //缓存永不过期，被动更新
                    redissonClient.getBucket("area" + i).set(data);
                }
            });
            log.info("缓存数据 更新 完成");
            //启动程序的时候需要等待首次更新缓存完成
            countDownLatch.countDown();
        }, 0, 30, TimeUnit.SECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("数据库 QPS为 : {}", atomicInteger.getAndSet(0));
        }, 0, 1, TimeUnit.SECONDS);

        countDownLatch.await();
    }

    /**
     * 总结：
     * 1、如果无法全量缓存所有数据，就只能使用第一种方案；
     * 2、无论使用第一种还是第二种方式都存在从数据库回溯数据到缓存，在往缓存中写入数据时，需要进行数据合法性校验，
     * 因为缓存不容易让我们发现原始数据问题，可能会造成生产事故,例如缓存中存储太多无效数据，就引出后面的缓存击穿问题。
     *
     * @return
     */
    @GetMapping("/getArea")
    public String getArea() {
        //随机查询一个城市
        int id = ThreadLocalRandom.current().nextInt(1000) + 1;
        String key = "area" + id;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String data = bucket.get();
        if (data == null) {
            //回源到数据库查询
            data = getCityFromDb(id);
            if (!StringUtils.isEmpty(data))
                //缓存30秒过期
                redissonClient.getBucket(key).set(data, 30, TimeUnit.SECONDS); }
        return data; }

    private String getCityFromDb(int cityId) {
        //模拟查询数据库，查一次增加计数器加一
        atomicInteger.incrementAndGet();
        return "areaData:" + System.currentTimeMillis();
    }

    /**
     * 缓存击穿问题：如果某些Key属于热点数据 ，在高并发场景下，如果Key过期，就会存在大量请求从数据库回溯，也就是大量并发请求直接打到了数据库，
     * 增大数据库压力，这种情况，就是我们常说的缓存击穿或缓存并发问题。
     *
     */
//    @PostConstruct
    public void init() {
        // 初始化一个热点数据key为hotData,并设置为5秒过期
        redissonClient.getBucket("com.hotData").set("hello_world",5, TimeUnit.SECONDS);
        // 每隔1秒输出一下数据库QPS
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("数据库 QPS为 : {}", atomicInteger.getAndSet(0));
            }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 运行之后发现，存在并发回溯数据库，每5秒大概有20左右，如果回溯对业务存在性能影响，可以通过加锁来实现
     * @return
     */
    @GetMapping("wrong")
    public String wrong() {
        RBucket<String> rBucket = redissonClient.getBucket("com.hotData");
        String data = rBucket.get();
        if (StringUtils.isEmpty(data)) {
            data = "hello_world";
            // 计数器加一，回溯一次数据库
            atomicInteger.incrementAndGet();
            //重新加入缓存，过期时间还是5秒
            redissonClient.getBucket("com.hotData").set(data, 5, TimeUnit.SECONDS);
        }
        return data;
    }

    @GetMapping("right")
    public String right() {
        RBucket<String> rBucket = redissonClient.getBucket("hotData");
        String data = rBucket.get();
        if (StringUtils.isEmpty(data)) {
            RLock locker = redissonClient.getLock("locker");
            //获取分布式锁
            if (locker.tryLock()) {
                try {
                    RBucket<String> bucket = redissonClient.getBucket("hotData");
                    data = bucket.get();
                    //双重检查，因为可能已经有一个B线程过了第一次判断，在等锁，然后A线程已经把数据写入了Redis中
                    if (StringUtils.isEmpty(data)) {
                        //回源到数据库查询
                        data = "hello_world";
                        // 计数器加一，回溯一次数据库
                        atomicInteger.incrementAndGet();
                        redissonClient.getBucket("hotData").set(data, 5, TimeUnit.SECONDS); }
                } finally {
                    //别忘记释放，另外注意写法，获取锁后整段代码try+finally，确保unlock万无一失
                    locker.unlock();
                }
            }
        }
        return data;
    }
    /**
     * 概括:实际业务中，不一定会这么严格使用分布式锁进行全局并发限制，虽然可以降低数据库并发回溯，但也限制了业务在缓存失效时的并发能力；
     * 可以采用：（1）使用进程内锁，每个节点可以存在一个线程进行数据库回溯；
     * （2）使用Semaphore信号量进行并发数限制，既限制了数据库回溯并发数，也能保证业务按照一定并发执行。
     */

    /**
     * 缓存击穿问题:当缓存中查找数据，如果查询不到，不一定数据在数据库中，可能数据本身就不存在，就会一直回溯数据库，
     * 增大数据库压力，影响应用使用；
     *
     * 如下：wrong1接口获取数据，如果传入的id在1到1000之间，就有数据返回，如果传入id为0就一直回溯，一直不能取到数据
     * @param id
     * @return
     */
    @GetMapping("wrong1")
    public String wrong1(@RequestParam("id") int id) {
        String key = "area" + id;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String data = bucket.get();
        //无法区分是无效用户还是缓存失效
        if (StringUtils.isEmpty(data)) {
            data = getCityFromDb1(id);
            redissonClient.getBucket(key).set(data, 30, TimeUnit.SECONDS);
        }
        return data;
    }

    /**
     * 但是这种方式可能会把大量无效的数据放入缓存，
     * 因此可以使用布隆过滤器进行处理
     * @param id
     * @return
     */
    @GetMapping("right1")
    public String right(@RequestParam("id") int id) {
        String key = "area" + id;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String data = bucket.get();
        if (StringUtils.isEmpty(data)) {
            data = getCityFromDb1(id);
            //校验从数据库返回的数据是否有效
            if (!StringUtils.isEmpty(data)) {
                redissonClient.getBucket(key).set(data, 30, TimeUnit.SECONDS);
            }
            else {
                //如果无效，直接在缓存中设置一个NODATA，这样下次查询时即使是无效用户还是可以命中缓存
                redissonClient.getBucket(key).set("NODATA", 30, TimeUnit.SECONDS);
            }
        }
        return data;
    }

    private BloomFilter<Integer> bloomFilter;

//    @PostConstruct
    public void init2() {
        //创建布隆过滤器，元素数量10000，期望误判率1%
        bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 10000, 0.01);
        //填充布隆过滤器
        IntStream.rangeClosed(1, 10000).forEach(bloomFilter::put);
    }

    /**
     * 使用布隆过滤器，先通过布隆过滤器进行一次判断；
     * 存在问题就是需要一开始就将可能的key填充到布隆过滤器，而且存在一定的误判机率，
     * 实际中可以把前面两种方案结合一起使用。
     * @param id
     * @return
     */
    @GetMapping("right2")
    public String right2(@RequestParam("id") int id) {
        String data = "";
        //通过布隆过滤器先判断
        if (bloomFilter.mightContain(id)) {
            String key = "area" + id;
            //走缓存查询
            RBucket<String> bucket = redissonClient.getBucket(key);
            data = bucket.get();
            if (StringUtils.isEmpty(data)) {
                //走数据库查询
                data = getCityFromDb1(id);
                redissonClient.getBucket(key).set(data, 30, TimeUnit.SECONDS);
            }
        }
        return data;
    }


    /**
     * 模拟数据库请求，请求一次变量atomicInteger就加一，不在1到1000之间的数据返回空串
     * @param id
     * @return
     */
    private String getCityFromDb1(int id) {
        atomicInteger.incrementAndGet();
        //注意，只有ID介于0（不含）和10000（包含）之间的用户才是有效用户，可以查询到用户信息
        if (id > 0 && id <= 10000) return "areaData";
        //否则返回空字符串
        return "";
    }

    /**
     * 总结：1、不能把redis完全当做数据库使用，因为缓存不一定可靠，存储数据量也有限，缓存数据必须落库存储；
     * 2、缓存性能远远高于数据库，需要考虑请求绕过缓存到达数据库，给DB带来的压力，如缓存雪崩、缓存击穿、缓存穿透问题；
     * 3、数据库与缓存之间数据同步策略的注意，先更新数据库再删除缓存，访问的时候按需加载数据到缓存”的策略是最为妥当的，详细的可以下来分析。
     */

}
