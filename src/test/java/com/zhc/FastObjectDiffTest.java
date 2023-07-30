package com.zhc;


import com.fastobject.diff.AbstractObjectDiff;
import com.fastobject.diff.DiffWapper;
import com.zhc.model.entity.Student;
import com.zhc.model.vo.UserVO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

        UserVO user2 = new UserVO("小丽","123456",18, studentList2);

        List<DiffWapper> diffWappers = AbstractObjectDiff.generateDiff(user1, user2);
        System.out.println(diffWappers.size());
        System.out.println(diffWappers.get(0).getDiffValue());
    }
}
