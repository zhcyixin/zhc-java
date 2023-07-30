package com.zhc.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhouhengchao
 * @since 2023-06-29 19:00:00
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class StudentRequest {

    private String name;

    private String stuNo;

    private Integer age;

}
