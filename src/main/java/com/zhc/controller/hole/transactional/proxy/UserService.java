package com.zhc.controller.hole.transactional.proxy;

import com.zhc.controller.hole.transactional.User;
import com.zhc.controller.hole.transactional.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

@Service
@Slf4j
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private UserService self;

    /**
     * 一个public 方法供Controller调用，内部调用控制事务的私有方法
     * 方法作用：传入一个用户名称创建一个用户
     * 返回对应名称的用户数
     * @param name
     * @return
     */
    public int createUserWrong1(String name) {
        try {
            this.createUserPrivate(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userMapper.findUserByName(name).size();
    }

    /**
     * 标记了@Transactional的private方法
     * @param user
     */
    @Transactional
    private void createUserPrivate(User user) {
        userMapper.insert(user);
        if (user.getName().contains("test"))
            throw new RuntimeException("invalid username!");
    }

    /**
     * UserService 中再建一个入口方法 createUserWrong2，来调用这个 public 方法再次尝试
     * @param name
     * @return
     */
    public int createUserWrong2(String name) {
        try {
            this.createUserPublic(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userMapper.findUserByName(name).size();
    }

    /**
     * 标记了@Transactional的public方法
     * @param user
     */
    @Transactional
    public void createUserPublic(User user) {
        userMapper.insert(user);
        if (user.getName().contains("test")){
            throw new RuntimeException("invalid username!");
        }
    }

    /**
     * 通过自己重新注入自己
     * @param name
     * @return
     */
    public int createUserRight(String name) {
        try {
            self.createUserPublic(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userMapper.findUserByName(name).size();
    }

    /**
     * 根据用户名-查询用户数
     * @param name
     * @return
     */
    public int getUserCount(String name) {
        return userMapper.findUserByName(name).size();
    }
}
