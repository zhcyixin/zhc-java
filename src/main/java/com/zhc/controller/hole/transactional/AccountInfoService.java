package com.zhc.controller.hole.transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

@Service
@Slf4j
public class AccountInfoService {

    @Resource
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private AccountInfoService self;

    /**
     * 一个public 方法供Controller调用，内部调用控制事务的私有方法
     * 方法作用：传入账号名称、密码创建一个账号
     * 返回对应对应密码的账户个数
     * @param name
     * @return
     */
    public int createAccountWrong1(String name,String password) {
        try {
            this.createAccountPrivate(new AccountInfo(name, password));
        } catch (Exception ex) {
            log.error("create account failed because {}", ex.getMessage());
        }
        return accountInfoMapper.findAccountByPassword(password).size();
    }

    /**
     * 标记了@Transactional的private方法
     * 当账号密码为123456时，抛出异常
     * @param accountInfo
     */
    @Transactional
    private void createAccountPrivate(AccountInfo accountInfo) {
        accountInfoMapper.insert(accountInfo);
        if (accountInfo.getPassword().contains("123456"))
            throw new RuntimeException("密码太简单，请重新传入复杂密码!");
    }

    /**
     * UserService 中再建一个入口方法 createUserWrong2，来调用这个 public 方法再次尝试
     * @param name
     * @return
     */
    public int createAccountWrong2(String name,String password) {
        try {
            this.createAccountPublic(new AccountInfo(name,password));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return accountInfoMapper.findAccountByPassword(name).size();
    }

    /**
     * 标记了@Transactional的public方法
     * @param accountInfo
     */
    @Transactional
    public void createAccountPublic(AccountInfo accountInfo) {
        accountInfoMapper.insert(accountInfo);
        if (accountInfo.getPassword().contains("123456")){
            throw new RuntimeException("密码太简单，请重新传入复杂密码!");
        }
    }

    /**
     * 通过自己重新注入自己
     * @param name
     * @return
     */
    public int createAccountRight(String name,String password) {
        try {
            self.createAccountPublic(new AccountInfo(name,password));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return accountInfoMapper.findAccountByPassword(password).size();
    }

    /**
     * 根据密码-查询账户数
     * @param password
     * @return
     */
    public int getAccountCount(String password) {
        return accountInfoMapper.findAccountByPassword(password).size();
    }
}
