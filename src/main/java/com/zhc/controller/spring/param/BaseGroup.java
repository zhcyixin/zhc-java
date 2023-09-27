package com.zhc.controller.spring.param;

import javax.validation.groups.Default;

/**
 * 基础校验分组
 * 注意：这里必须继承javax.validation.groups.Default,否则在使用groups之后，其他未使用groups的校验会不生效
 */
public interface BaseGroup extends Default {

    interface Base {};

    interface Insert {};

    interface Update {};
}
