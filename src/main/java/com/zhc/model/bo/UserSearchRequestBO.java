package com.zhc.model.bo;

import com.zhc.model.request.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息查询BO
 * @author zhouhengchao
 * @since 2023-06-29 19:00:00
 * @version 1.0
 */
@Data
public class UserSearchRequestBO extends PageRequest {

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 创建时间-开始
     */
    private Date createTimeStart;

    /**
     * 创建时间-结束
     */
    private Date createTimeEnd;

}
