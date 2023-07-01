package com.zhc.mybatis;

import com.github.pagehelper.PageHelper;
import com.zhc.dao.StudentMapper;
import com.zhc.model.entity.Student;
import com.zhc.model.vo.StudentCourseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/v1/zhc/java/mybatis/student/")
public class StudentController {

    @Resource
    private StudentMapper studentMapper;

    @PostMapping("queryList")
    public List<StudentCourseVo> queryStudent(){
        PageHelper.startPage(1, 10);
        return studentMapper.selectStudentList();
    }
}
