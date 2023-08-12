package com.zhc.demo;

import com.fastobject.diff.AbstractObjectDiff;
import com.fastobject.diff.ChineseObjectDiff;
import com.fastobject.diff.DiffWapper;
import com.zhc.model.vo.UserRoleVO;
import com.zhc.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 相信很多小伙伴在工作中，会遇到需要比较两个相同类型对象中值得差异性需求，例如：在用户信息修改时，记录审计日志，
 * 要比较两个用户对象中哪些属性的值发生过变化，修改前和修改后的值，如：姓名由小明改为小军，年龄由18改为19，类似这样的输出效果
 * 今天就给大家分享一种简单的实现方法，通过 fast-object-diff实现对象数据比对
 *
 * @author zhouhengchao
 * @since 2023-08-11 10:14:00
 */
@Slf4j
public class FastObjectDiffTest {

    /**
     * 构建两个UserVO对象user1、user2，分别为对象赋值，然后使用工具类进行比较
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        // 创建user1对象，设置参数值
        List<UserRoleVO> userRoleVOList1 = new ArrayList<>();
        UserRoleVO userRoleVO1 = new UserRoleVO(1,"admin","管理员");
        userRoleVOList1.add(userRoleVO1);

        UserVO user1 = new UserVO("小红","1234567",20,new Date(), userRoleVOList1);

        // 创建user2对象，设置参数值
        List<UserRoleVO> userRoleVOList2 = new ArrayList<>();
        UserRoleVO userRoleVO2 = new UserRoleVO(1,"admin","测试人员");
        userRoleVOList2.add(userRoleVO2);

        UserVO user2 = new UserVO("小丽","123456",18, new Date(),userRoleVOList2);
        // 调用对比方法，进行数据对比
        String result = dataDiffUtil(user1,user2);
        // String result = AbstractObjectDiff.genChineseDiffStr(user1, user2);
        log.info("比对结果为:{}",result);
    }

    /**
     * 数据对象比较方法
     * @param oldObject 原始数据
     * @param newObject 修改后的数据
     * @return
     * @throws Exception
     */
    private String dataDiffUtil(Object oldObject, Object newObject) throws Exception {
        /**
         * 通过调用 AbstractObjectDiff类的generateDiff方法对两个对象进行数据比较，会对具有@DiffLog注解的字段进行比对；
         * 比对后将有差异的数据字段通过List<DiffWapper> 进行返回，如果diffWrappers为空，则不存在数据差异
         */
        List<DiffWapper> diffWrappers = AbstractObjectDiff.generateDiff(oldObject, newObject);

        if(CollectionUtils.isEmpty(diffWrappers)){
            return "";
        }
        /**
         * 如果返回的diffWrappers不为空，则说明存在数据差异，通过循环遍历，拼接为需要的输出形式
         */
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < diffWrappers.size(); i++){
            DiffWapper diffWapper = diffWrappers.get(i);
            stringBuilder.append(diffWapper.getLogName())
                    .append(":由").append(diffWapper.getDiffValue().getOldValue())
                    .append(",更为:").append(diffWapper.getDiffValue().getNewValue())
                    .append(";");
        }
        return stringBuilder.toString();
    }
}
