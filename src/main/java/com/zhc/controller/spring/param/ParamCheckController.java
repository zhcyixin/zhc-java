package com.zhc.controller.spring.param;

import com.alibaba.fastjson.JSON;
import com.zhc.exception.BusinessException;
import com.zhc.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 在使用Spring开发过程中，我们经常会进行参数校验，严格的参数校验可以避免系统出现bug和安全漏洞，参数校验主要使用依赖
 * <dependency>
 *     <groupId>org.hibernate.validator</groupId>
 *     <artifactId>hibernate-validator</artifactId>
 * </dependency>
 * 也可以使用 spring-boot-starter-validation
 * <dependency>
 *   <groupId>org.springframework.boot</groupId>
 *   <artifactId>spring-boot-starter-validation</artifactId>
 * </dependency> 以及spring自带的校验机制
 *
 * 使用注解进行参数校验，可以避免在controller层或者service层编写大量的if else代码，使代码更加简洁易读
 * 今天分享的三个场景案例：
 * 1、使用hibernate.validator 内置注解进行常规参数校验，数据分组校验；
 * 2、通过自定义注解，实现参数传参值是否符合规定校验；
 * 3、通过自定义注解 + ControllerAdvice + RequestBodyAdvice实现对请求参数的转换
 *
 * @author zhouhengchao
 * @since 2023-09-25 15:45:00
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/spring/paramCheck")
public class ParamCheckController {

    /**
     * 新增用户信息，对请求参数校验
     * @param userInfo
     * @return
     */
    @PostMapping("/addUserInfo")
    public Result<UserInfo> addUser(@Valid @RequestBody UserInfo userInfo){
        userInfo.checkIdCardAndRealName();
        log.info("新增用户传入的参数为:{}", JSON.toJSONString(userInfo));
        return Result.success(userInfo);
    }

    /**
     * 编辑用户信息，校验用户参数
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/updateUserInfo")
    public Result<UserInfo> updateUser(@Validated({BaseGroup.Update.class, BaseGroup.Base.class}) @RequestBody UserInfo userInfo){
        log.info("修改传入的参数为:{}", JSON.toJSONString(userInfo));
        return Result.success(userInfo);
    }

    /**
     * 总结：
     * 1、合理的使用hibernate-validator进行参数校验，可以简化代码，提高代码可读性；
     * 2、对于部分定制化校验，可以通过自定义注解，并实现ConstraintValidator接口，编写参数校验器实现；
     * 3、对于请求参数数据转化，可以通过自定义注解，并实现RequestBodyAdvice接口，在对应方法中转换参数，实现与业务代码间的解耦合；
     * 4、对比一下Valid与Validated：
     * （1）Valid：可以用在方法、构造函数、方法参数和成员属性（field）上，Validated：用在类、方法和方法参数上。但不能用于成员属性（field）；
     * （2）Valid：没有分组功能，Validated：提供分组功能，可以在参数验证时，根据不同的分组采用不同的验证机制；
     * （3）Valid 可以嵌套验证，Validated不能嵌套验证
     */
}
