package com.zhc.controller.hole.transactional;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


/**
 * 定义一个用户实体类，包括主键id、用户姓名
 * @author zhouhengchao
 * @since 2023-07-08 16:27:00
 * @version 1.0
 */
@Data
public class User {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户姓名
     */
    private String name;

    public User(String name){
        this.name = name;
    }
}
