package com.zhc.controller.exception;

import com.zhc.model.exception.request.ExceptionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhengchao
 * @since 2023-06-10 20:49:00
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/zhc/java/exception")
public class ExceptionController {

    @PostMapping("/dingPush")
    public String generateException(@RequestBody ExceptionRequest request){
        return request.getParam1().substring(0,6);
    }
}
