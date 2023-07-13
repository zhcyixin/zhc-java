package com.zhc.controller.hole.transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring声明式事务中的坑
 *
 * @author zhouhengchao
 * @since 2023-07-12 14:57:00
 */
@RestController
@RequestMapping("/v1/zhc/java/hole/transaction")
@Slf4j
public class TransactionController {

    @Autowired
    private AccountInfoService accountInfoService;

    /**
     * 调用接口后发现：即便密码为123456，账户也能创建成功
     * 原因分析：只有定义在 public方法上的 @Transactional 事务才能生效；
     * 因为Spring 默认通过动态代理的方式实现 AOP，对目标方法进行增强，private方法无法代理到，Spring 自然也无法动态增强事务处理逻辑
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name, @RequestParam("password") String password) {
        try {
            accountInfoService.createAccountWrong1(name, password);
        } catch (Exception ex) {
            log.error("创建账号失败, 因为:{}", ex.getMessage());
        }
        return accountInfoService.getAccountCount(password);
    }

    /**
     * 可能很容易想到：把标记了事务注解的 createAccountPrivate 方法改为 public 即可
     * 测试发现: 调用新的 createAccountWrong2 方法事务同样不生效
     * 原因分析：@Transactional 生效原则 2，必须通过代理过的类从外部调用目标方法才能生效
     * Spring 通过 AOP 技术对方法进行增强，要调用增强过的方法必然是调用代理后的对象
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name, @RequestParam("password") String password) {
        try {
            accountInfoService.createAccountWrong2(name, password);
        } catch (Exception ex) {
            log.error("创建账号失败, 原因:{}", ex.getMessage(), ex);
        }
        return accountInfoService.getAccountCount(password);
    }

    /**
     * 注入一个 self，然后再通过 self 实例调用标记有 @Transactional 注解的 createAccountPublic 方法
     * 结果发现：可以验证事务是生效的
     * @param name
     * @param password
     * @return
     */
    @GetMapping("right1")
    public int right1(@RequestParam("name") String name, @RequestParam("password") String password) {
        return accountInfoService.createAccountRight(name, password);
    }

    /**
     * 虽然在 AccountInfoService 内部注入自己调用自己的 createAccountPublic 可以正确实现事务，
     * 但更合理的实现方式是，让 Controller 直接调用之前定义的 AccountInfoService 的 createAccountPublic 方法，
     * 因为注入自己调用自己很奇怪，也不符合分层实现的规范
     * @param name
     * @return
     */
    @GetMapping("right2")
    public int right2(@RequestParam("name") String name, @RequestParam("password") String password) {
        try {
            accountInfoService.createAccountPublic(new AccountInfo(name,password));
        } catch (Exception ex) {
            log.error("创建账号失败： {}", ex.getMessage());
        }
        return accountInfoService.createAccountRight(name, password);
    }

    /**
     * 总结：
     * 1、使用spring声明式事务注解@Transactional必须作用在public修饰的方法上，必须通过spring的bean注入才能生效；
     * 2、事务和异常配合使用时要小心，事务被异常给吞掉了；
     * 3、注意使用事务作用的范围，不要直接将注解@Transactional作用在一个很长逻辑的方法上，否则可能会出现长事务问题，长事务
     * 问题会导致整个服务卡顿，进一步导致应用崩溃，应该将事务的粒度尽可能拆细一些，查询类操作放事务外。
     *
     *
     */
}
