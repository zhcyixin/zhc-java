package com.zhc.model.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户信息表(User)实体类
 *
 * @author makejava
 * @since 2023-11-29 10:57:45
 */
public class User implements Serializable {
    private static final long serialVersionUID = -22734552782737304L;
    /**
     * 主键Id
     */
    private Integer id;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别，1男 2女 0 未知
     */
    private Integer gender;
    /**
     * 状态，0 禁用 1启用
     */
    private Integer status;
    /**
     * 是否删除
     */
    private Byte isDeleted;
    /**
     * 用户来源
     */
    private Integer source;
    /**
     * 身份证号
     */
    private String idCard;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

}

