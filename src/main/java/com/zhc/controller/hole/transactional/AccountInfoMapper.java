package com.zhc.controller.hole.transactional;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountInfoMapper extends BaseMapper<AccountInfo> {

    /**
     * 根据密码查询账户列表信息
     * @param password
     * @return
     */
    List<AccountInfo> findAccountByPassword(@Param("password") String password);
}
