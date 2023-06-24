package com.zhc.controller.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/rlock")
public class RLockController {

    // 定义一个锁前缀
    private final String LOCK_KEY_PRE = "redission_lock_prefix";

    private final String REDIS_STOCK = "redis_stock";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 库存中有200件商品
    private Integer totalNum = 200;

    /**
     * 初始化100个库存商品
     */
    @GetMapping("/initStock")
    public String initStock(){
        RBucket<Integer> bStock = redissonClient.getBucket(REDIS_STOCK);
        bStock.set(totalNum, 2, TimeUnit.DAYS);
        return "初始化库存成功";
    }

    /**
     * 初始化100个库存商品
     */
    @GetMapping("/getStock")
    public String getStock(){

        RBucket<Integer> bStock = redissonClient.getBucket(REDIS_STOCK);
        return String.valueOf(bStock.get());
    }

    /**
     * 扣减库存，通过synchronized加锁，只能在同一个jvm下；
     * 对于多个tomcat部署不生效
     */
    @GetMapping("/test01")
    public void test01(){
        for (int i = 0; i < 6; i++) {
            new Thread(this::reduceStock).start();
        }
    }

    /**
     * 使用redis最基础的方式实现锁
     * 存在两个问题：1、如果setIfAbsent加锁成功，但是到业务逻辑代码时，该服务挂掉了，
     * 就会导致另一个服务一直获取不到锁，一直在等待中；可以使用 redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey,30,TimeUnit.SECONDS);
     * 设置过期时间，保证及时服务down掉，其他应用在锁过期时也能获取到锁；
     * 2、细心的朋友可能会发现，如果key过期的时间早于程序执行时间，会导致删除key失败，多应用间释放锁，导致锁一直失效；
     *
     */
    @GetMapping("/test02")
    public void test02(){
        // 分布式锁名称,关键是多个应用要共享这一个Redis的key
        String lockKey = "lockDeductStock";
        // setIfAbsent 如果不存在key就set值，并返回1
        //如果存在(不为空)不进行操作，并返回0,与redis命令setnx相似，setIfAbsent是java中的方法
        // 根据返回值为1就表示获取分布式锁成功，返回0就表示获取锁失败
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey);
        // 加锁不成功，返回给前端错误码，前端给用户友好提示
        if (Boolean.FALSE.equals(lockResult)) {
            log.info("系统繁忙，请稍后再试！");
            return;
        }
        reduceStock();
        // 业务执行完成，删除这个锁
        redisTemplate.delete(lockKey);
    }

    /**
     * 1、锁被勿释放问题，通过添加UUID来解决；
     * 2、锁超时问题，写一个定时任务，分线程每隔十秒去查看一次主线程是否持有这把锁，
     * 如果这个锁存在，重新将这个锁的超时时间设置为30S，对锁延时，比较复杂
     */
    @GetMapping("/test03")
    public void test03(){
        // 分布式锁名称,关键是多个应用要共享这一个Redis的key
        String lockKey = "lockDeductStock";
        // 分布式锁的值
        String lockValue = UUID.randomUUID().toString().replaceAll("-", "");
        // setIfAbsent 如果不存在key就set值，并返回1
        //如果存在(不为空)不进行操作，并返回0,与redis命令setnx相似，setIfAbsent是java中的方法
        // 根据返回值为1就表示获取分布式锁成功，返回0就表示获取锁失败
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
        // 加锁不成功，返回给前端错误码，前端给用户友好提示
        if (Boolean.FALSE.equals(lockResult)) {
            log.info("系统繁忙，请稍后再试！");
        }
        reduceStock();
        // 判断是不是当前请求的UUID，如果是则可以正常释放锁。如果不是，则释放锁失败！
        if (lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 完美解决方案，使用redisson
     */
    @GetMapping("/test04")
    public void test04(){
        // 分布式锁名称,关键是多个应用要共享这一个Redis的key
        String lockKey = "lockDeductStock";
        // 获取锁对象
        RLock redissonLock = redissonClient.getLock(lockKey);
        try {
            redissonLock.lock();
//            boolean result = redissonLock.tryLock();
            // 加锁不成功，返回给前端错误码，前端给用户友好提示
//            if (!result) {
//                log.info("系统繁忙，请稍后再试！");
//                return;
//            }
            reduceStock();
        }
        finally{
            if(redissonLock.isHeldByCurrentThread()){
                redissonLock.unlock();
            }
        }
    }

    /**
     * 从redis中获取库存，扣减库存数量
     */
    private void reduceStock(){
        synchronized (this) {
            // 从redis中获取商品库存
            RBucket<Integer> bucket = redissonClient.getBucket(REDIS_STOCK);
            int stock = bucket.get();
            if (stock > 0) {
                // 库存-1
                stock--;
                // 更新库存
                bucket.set(stock, 2, TimeUnit.DAYS);
                log.info("扣减成功，剩余库存：" + stock);
            } else {
                log.info("扣减失败，库存不足");
            }
        }
    }

}
