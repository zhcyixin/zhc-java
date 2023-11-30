package com.zhc.service.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhc.dao.UserDao;
import com.zhc.model.bo.UserListBO;
import com.zhc.model.bo.UserSearchRequestBO;
import com.zhc.model.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements AdminUserService{

    @Resource
    private UserDao userDao;

    @Override
    public PageInfo<UserListBO> findUserList(UserSearchRequestBO userSearchRequestBO){
        PageHelper.startPage(userSearchRequestBO.getPageNum(), userSearchRequestBO.getPageSize());
        List<User> userList = userDao.findUserList(userSearchRequestBO);
        if(CollectionUtils.isEmpty(userList)){
            return new PageInfo<>(Lists.newArrayList());
        }
        List<UserListBO> userListBOList = userList.stream().map(item -> {
            UserListBO userListBO = new UserListBO();
            BeanUtils.copyProperties(item, userListBO);
            return userListBO;
        }).collect(Collectors.toList());

        return new PageInfo<>(userListBOList);
    }
}
