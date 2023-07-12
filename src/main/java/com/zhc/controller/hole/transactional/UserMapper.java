package com.zhc.controller.hole.transactional;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据名称查询用户信息
     * @param name
     * @return
     */
    List<User> findUserByName(@Param("name") String name);
}
