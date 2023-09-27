package com.zhc.controller.spring.param;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})// 只允许用在字段上
@Retention(RetentionPolicy.RUNTIME) //注解保留在程序允许期间，可通过反射获取类上所有注解
@Constraint(validatedBy = ParamConstraintValidated.class) // 配置绑定注解关联的验证器
public @interface CheckParam {

    /**
     * 参数允许的值
     * @return
     */
    String [] paramAllowValues();

    /**
     * 异常提示信息
     */
    String message() default "参数值不合法，请检查";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
