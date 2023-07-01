package com.zhc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhc.model.entity.Student;
import com.zhc.model.request.OtherPageRequest;
import com.zhc.model.request.PageRequest;
import com.zhc.model.vo.StudentCourseVo;

import java.util.List;

public interface StudentMapper extends BaseMapper<Student> {

    List<StudentCourseVo> selectStudentList();

    List<StudentCourseVo> selectStudentList1();

    List<StudentCourseVo> selectStudentList2(PageRequest request);

    List<StudentCourseVo> selectStudentList3(OtherPageRequest request);

    List<StudentCourseVo> selectStudentList4(OtherPageRequest request);

    Integer selectStudentList1_COUNT();
}
