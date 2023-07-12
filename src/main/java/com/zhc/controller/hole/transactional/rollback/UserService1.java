package com.zhc.controller.hole.transactional.rollback;

import com.zhc.controller.hole.transactional.User;
import com.zhc.controller.hole.transactional.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class UserService1 {
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void createUserWrong1(String name) {
        try {
            userMapper.insert(new User(name));
            throw new RuntimeException("error");
        } catch (Exception ex) {
            log.error("create user failed", ex);
        }
    }

    @Transactional
    public void createUserWrong2(String name) throws IOException {
        userMapper.insert(new User(name));
        otherTask();
    }

    private void otherTask() throws IOException {
        Files.readAllLines(Paths.get("file-that-not-exist"));
    }

    public int getUserCount(String name) {
        return userMapper.findUserByName(name).size();
    }


    @Transactional
    public void createUserRight1(String name) {
        try {
            userMapper.insert(new User(name));
            throw new RuntimeException("error");
        } catch (Exception ex) {
            log.error("create user failed", ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("result {} ", userMapper.findUserByName(name).size());//为什么这里是1你能想明白吗？
    }

    //DefaultTransactionAttribute
    @Transactional(rollbackFor = Exception.class)
    public void createUserRight2(String name) throws IOException {
        userMapper.insert(new User(name));
        otherTask();
    }

}
