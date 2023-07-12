package com.zhc.controller.hole.transactional.proxy;

import com.zhc.controller.hole.transactional.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/zhc/java/hole/transactionpropagation")
@Slf4j
public class TransactionPropagationController {

    @Autowired
    private UserService userService;

    /**
     * 调用接口后发现：即便用户名不合法，用户也能创建成功。刷新浏览器，多次发现有十几个的非法用户注册
     * 原因分析：@Transactional 生效原则 1，除非特殊配置（比如使用 AspectJ 静态织入实现 AOP），否则只有定义在 public
     * 方法上的 @Transactional 才能生效；
     * 2、Spring 默认通过动态代理的方式实现 AOP，对目标方法进行增强，private 方法无法代理到，Spring 自然也无法动态增强事务处理逻辑
     * @param name
     * @return
     */
    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name) {
        try {
            userService.createUserWrong1(name);
        } catch (Exception ex) {
            log.error("createUserWrong failed, reason:{}", ex.getMessage());
        }
        return userService.getUserCount(name);
    }

    /**
     * 可能很容易想到：把标记了事务注解的 createUserPrivate 方法改为 public 即可
     * 测试发现: 调用新的 createUserWrong2 方法事务同样不生效
     * 原因分析：@Transactional 生效原则 2，必须通过代理过的类从外部调用目标方法才能生效
     * Spring 通过 AOP 技术对方法进行增强，要调用增强过的方法必然是调用代理后的对象
     * @param name
     * @return
     */
    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name) {
        try {
            userService.createUserWrong2(name);
        } catch (Exception ex) {
            log.error("createUserWrong2 failed, reason:{}", ex.getMessage(), ex);
        }
        return userService.getUserCount(name);
    }

    /**
     * 注入一个 self，然后再通过 self 实例调用标记有 @Transactional 注解的 createUserPublic 方法
     * 结果发现：可以验证事务是生效的，非法的用户注册操作可以回滚
     * @param name
     * @return
     */
    @GetMapping("right1")
    public int right1(@RequestParam("name") String name) {
        return userService.createUserRight(name);
    }

    /**
     * 虽然在 UserService 内部注入自己调用自己的 createUserPublic 可以正确实现事务，
     * 但更合理的实现方式是，让 Controller 直接调用之前定义的 UserService 的 createUserPublic 方法，
     * 因为注入自己调用自己很奇怪，也不符合分层实现的规范
     * @param name
     * @return
     */
    @GetMapping("right2")
    public int right2(@RequestParam("name") String name) {
        try {
            userService.createUserPublic(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userService.getUserCount(name);
    }
}
