package com.zhc.service.user;

import com.github.pagehelper.PageInfo;
import com.zhc.model.bo.UserListBO;
import com.zhc.model.bo.UserSearchRequestBO;


public interface AdminUserService {

    PageInfo<UserListBO> findUserList(UserSearchRequestBO userSearchRequestBO);
}
