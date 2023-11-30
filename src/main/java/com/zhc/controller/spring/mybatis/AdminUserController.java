package com.zhc.controller.spring.mybatis;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhc.model.bo.UserListBO;
import com.zhc.model.bo.UserSearchRequestBO;
import com.zhc.model.request.UserSearchRequest;
import com.zhc.model.vo.UserListVO;
import com.zhc.service.user.AdminUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring优雅编程系列5
 * 基于Spring boot + Mybatis实现的用户信息增删改查
 * 通过集成Swagger生成接口文档
 *
 * @author zhouhengchao
 * @since 2023-11-29 13:40
 */

@Api(value = "用户操作相关接口", tags = "用户接口")
@RestController
@RequestMapping("/v1/zhc/java/spring/user")
public class AdminUserController {

    @Resource
    private AdminUserService adminUserService;

    @ApiOperation("用户列表接口")
    @PostMapping("/findUserList")
    public PageInfo<UserListVO> findUserList(@RequestBody UserSearchRequest request){
        UserSearchRequestBO userSearchRequestBO = new UserSearchRequestBO();
        BeanUtils.copyProperties(request, userSearchRequestBO);
        PageInfo<UserListBO> pageInfo = adminUserService.findUserList(userSearchRequestBO);
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            return new PageInfo<>(Lists.newArrayList());
        }
        List<UserListBO> userList = pageInfo.getList();
        List<UserListVO> userListVOList = userList.stream().map(item -> {
            UserListVO userListVO = new UserListVO();
            BeanUtils.copyProperties(item, userListVO);
            return userListVO;
        }).collect(Collectors.toList());
        return new PageInfo<>(userListVOList);
    }
}
