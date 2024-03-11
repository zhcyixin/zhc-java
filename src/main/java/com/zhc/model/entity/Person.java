package com.zhc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 *
 * @author zhouhengchao
 * @since 2024-03-01 15:29:00
 */
@Document(collection = "person")
@Data
@AllArgsConstructor
public class Person {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 所在城市
     */
    private String cityName;

}
