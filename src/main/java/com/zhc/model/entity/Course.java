package com.zhc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author zhouhengchao
 * @since 2023-06-29 19:00:00
 * @version 1.0
 */
@Data
public class Course {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String studentId;

    private String courseName;

    private String courseType;

    private Integer alreadyHours;
}
