package com.zhc.controller.hole.transactional;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


/**
 * 定义一个账户实体类，包括主键id、账号名称、密码
 * @author zhouhengchao
 * @since 2023-07-08 16:27:00
 * @version 1.0
 */
@Data
public class AccountInfo {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 账号名称
     */
    private String name;

    /**
     * 账号密码
     */
    private String password;

    public AccountInfo(String name,String password){
        this.name = name;
        this.password = password;
    }
}
