package com.zhc.controller.spring.param;

import com.zhc.exception.BusinessException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

/**
 * 参数校验demo请求实体
 * @author zhouhengchao
 * @since 2023-09-25 16:27:00
 */
@Data
public class UserInfo {

    @NotNull(groups = {BaseGroup.Update.class}, message = "用户id不能为空")
    private Integer id;

    @NotNull(message = "用户名不能为空")
    @Size(min = 6, max = 20, message = "用户名长度必须在6到20字符之间")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "1[3-9][0-9]{9}", message = "手机号格式不正确")
    @Size(max = 11, min = 11, message = "手机号长度为11")
    private String mobile;

    @Email(message = "邮箱格式不合法")
    private String email;

    @Range(min = 1, max = 120,message = "年龄参数只能在1到120之间")
    private Integer age;

    /**
     * 身份证号和姓名不能同时为空，该如何校验，
     * 可以通过在请求中定义方法
     */
    private String idCard;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别，只能为man和woman
     * 使用自定义注解CheckParam进行参数校验
     */
    @CheckParam(paramAllowValues = {"man","woman"}, message = "性别参数只能传入man和woman")
    private String gender;

    /**
     * 照片地址,需要对地址进行统一转化，转为Base64形式
     * 可以通过自定义注解 + ControllerAdvice + RequestBodyAdvice实现对请求参数的转换
     */
    @ConvertPathToBase64
    private String photoImage;

    public void checkIdCardAndRealName(){
        if(StringUtils.isAllBlank(this.idCard,this.realName)){
            throw new BusinessException("身份证号和姓名不能同时为空。");
        }
    }

}
