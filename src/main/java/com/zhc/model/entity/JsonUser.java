package com.zhc.model.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 该对象主要用于Json对象序列化使用测试
 *
 * @author zhouhengchao
 * @since 2023-12-1 10:13:00
 */
@Data
@JsonIgnoreProperties(value = {"gender"})
public class JsonUser {

    /**
     * 用户名
     * Json字符串中名称与对象名称不一致，使用@JsonProperty("userName")注解
     */
    @JsonProperty("userName")
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     * 该字段只参与反序列化不参与序列化
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mobile;

    /**
     * 备注
     * 该字段不参与Json序列化与反序列化，使用@JsonIgnoreProperties注解
     */
    @JsonIgnore
    private String memo;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date birthDay;

    /**
     * 技能列表
     */
    private List<String> skillsList;

    @JsonGetter("userEmail")
    public String getEmail(){
        return email;
    }
    /**
     * 小结：
     * 1、在Json反序列化时，如果需要给某个字段重新命名，使用@JsonProperty注解；
     * 2、需要完全忽略某个属性，使用@JsonIgnore或JsonIgnoreProperties，前者用于方法或字段上，后者用于类上；
     * 3、如何某个字段只是序列化时忽略，可以使用@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)，
     * 反序列化时忽略，可以使用@JsonProperty(access = JsonProperty.Access.READ_ONLY)
     * 4、如果需要对请求参数日期进行格式化，需要使用@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
     */
}
