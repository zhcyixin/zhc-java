package com.zhc.model.vo;

import lombok.Data;

@Data
public class StudentCourseVo {

    private Integer id;

    private String name;

    private String stuNo;

    private Integer age;

    private String courseName;

    private String courseType;

    private Integer alreadyHours;
}
