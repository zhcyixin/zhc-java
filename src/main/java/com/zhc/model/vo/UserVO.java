package com.zhc.model.vo;

import com.fastobject.diff.DiffLog;
import com.zhc.model.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserVO {

    @DiffLog(name = "姓名")
    private String name;

    @DiffLog(name = "密码")
    private String password;

    @DiffLog(name = "年龄")
    private Integer age;

    private List<Student> studentList;
}
