package com.zhc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCourseVo {

    private Integer id;

    private String name;

    private String stuNo;

    private Integer age;

    private String courseName;

    private String courseType;

    private Integer alreadyHours;
}
