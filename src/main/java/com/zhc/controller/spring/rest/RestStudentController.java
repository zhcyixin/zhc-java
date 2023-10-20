package com.zhc.controller.spring.rest;

import com.alibaba.fastjson.JSON;
import com.zhc.model.entity.Student;
import org.springframework.web.bind.annotation.*;

/**
 * 模拟rest请求类
 *
 * @author zhouhengchao
 * @since 2023-10-20 11:40:00
 */
@RestController
@RequestMapping("/v1/zhc/java/spring/rest")
public class RestStudentController {

    @GetMapping("/findStudent")
    public Student findStudentList(@RequestParam Integer id){
        System.out.println("id:"+id);
        return Student.builder()
                .id(1)
                .name("小花")
                .age(18).stuNo("20231020").build();
    }

    @PostMapping("postFindStudent")
    public Student findStudent(@RequestBody Student student){
        System.out.println("student:"+ JSON.toJSONString(student));
        return Student.builder()
                .id(1)
                .name("小花")
                .age(18).stuNo("20231020").build();
    }
}
