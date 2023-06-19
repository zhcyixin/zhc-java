package com.zhc.controller.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/rlock")
public class RLockController {

    // 定义一个锁前缀
    private final String LOCK_KEY_PRE = "redission_lock_prefix";

    @Resource
    private RedissonClient redissonClient;

    // 库存中有100件商品
    private Integer totalNum = 100;

    /**
     *
     * 该接口只允许有一个人可以调用
     * @return
     */
    @GetMapping("/allLock")
    public String allLock(){
        RLock rLock = redissonClient.getLock(LOCK_KEY_PRE);
        try {
            boolean res = rLock.tryLock();
            if(!res){
                log.info("手速太快了，请休息一下。");
                throw new RuntimeException("手速太快了，请休息一下。");
            }
            totalNum --;
            log.info("当前库存数量为:{}",totalNum);
        }catch(Exception e){

        } finally{
            rLock.unlock();
        }
        return "程序执行成功";
    }
}
