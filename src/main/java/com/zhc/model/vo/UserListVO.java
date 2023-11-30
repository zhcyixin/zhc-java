package com.zhc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息列表VO
 *
 * @author zhouhengchao
 * @since 2023-11-29 10:57:45
 */
@Data
@ApiModel(value = "用户列表VO", description = "用户列表相关信息")
public class UserListVO implements Serializable {
    private static final long serialVersionUID = -22734552782737304L;
    /**
     * 主键Id
     */
    @ApiModelProperty("ID")
    private Integer id;

    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    private String name;

    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 用户手机号
     */
    @ApiModelProperty("用户手机号")
    private String mobile;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;
    /**
     * 性别，1男 2女 0 未知
     */
    @ApiModelProperty("性别")
    private Integer gender;

    /**
     * 状态，0 禁用 1启用
     */
    @ApiModelProperty("状态，0 禁用 1启用")
    private Integer status;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Byte isDeleted;

    /**
     * 用户来源
     */
    @ApiModelProperty("用户来源")
    private Integer source;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String idCard;
}

