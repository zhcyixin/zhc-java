package com.zhc.model.vo;

import com.fastobject.diff.DiffLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 定义一个UserVO对象，在需要进行比对的字段上加上@DiffLog注解，通过name属性配置对应打印出来的值
 */
@Data
@AllArgsConstructor
public class UserVO {

    @DiffLog(name = "姓名")
    private String name;

    @DiffLog(name = "密码")
    private String password;

    @DiffLog(name = "年龄")
    private Integer age;

    @DiffLog(name = "更新时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @DiffLog(name = "用户所属角色列表")
    private List<UserRoleVO> userRoleVOList;
}
