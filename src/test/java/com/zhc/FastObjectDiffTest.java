package com.zhc;


import com.fastobject.diff.AbstractObjectDiff;
import com.fastobject.diff.DiffWapper;
import com.zhc.model.entity.Student;
import com.zhc.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FastObjectDiffTest {

    @Test
    public void test1() throws Exception {
        List<Student> studentList1 = new ArrayList<>();
        Student student1 = new Student(1,"红红","123",18);
        studentList1.add(student1);

        UserVO user1 = new UserVO("小红","123456",18, studentList1);

        List<Student> studentList2 = new ArrayList<>();
        Student student2 = new Student(1,"红红","123",18);
        studentList2.add(student2);

        UserVO user2 = new UserVO("小红","123456",18, studentList2);

        String result = DataDiffUtil(user1,user2);
        log.info("比对结果为:{}",result);
    }

    private String DataDiffUtil(Object oldObject, Object newObject) throws Exception {
        List<DiffWapper> diffWappers = AbstractObjectDiff.generateDiff(oldObject, newObject);
        if(CollectionUtils.isEmpty(diffWappers)){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < diffWappers.size(); i++){
            DiffWapper diffWapper = diffWappers.get(i);
            stringBuilder.append(diffWapper.getLogName()).append(":由").append(diffWapper.getDiffValue().getOldValue())
                    .append(",更为:").append(diffWapper.getDiffValue().getNewValue()).append(";");
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
