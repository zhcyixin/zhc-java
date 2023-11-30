package com.zhc.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhouhengchao
 * @since 2023-06-29 19:00:00
 * @version 1.0
 */
@Data
@ApiModel(value = "用户信息查询请求")
public class UserSearchRequest extends PageRequest{

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户手机号")
    private String mobile;

    @ApiModelProperty("创建时间-开始")
    private Date createTimeStart;

    @ApiModelProperty("创建时间-结束")
    private Date createTimeEnd;

}
