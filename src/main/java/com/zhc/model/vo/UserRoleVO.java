package com.zhc.model.vo;

import com.fastobject.diff.DiffLog;
import com.fastobject.diff.DiffLogKey;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户所属角色列表，通过userId进行关联
 */
@Data
@AllArgsConstructor
public class UserRoleVO {

    /**
     * 主键id
     */
    private Integer id;

    @DiffLogKey(name = "角色编号")
    @DiffLog(name = "角色code")
    private String roleCode;

    @DiffLog(name = "角色名称")
    private String roleName;
}
