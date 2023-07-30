package com.zhc.controller.signature;

import com.zhc.model.Result;
import com.zhc.model.request.StudentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 接口鉴权测试Controller类
 * @author zhouhengchao
 * @since 2023-07-23 17:04:00
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/signature")
public class SignatureController {

    /**
     * 如果通过鉴权就返回字符串 Hello world signature加上学生名字.
     */
    @GetMapping("/testSignature")
    public Result<String> testSignature(@RequestBody StudentRequest studentRequest){
        return Result.success("Hello world signature:" + studentRequest.getName());
    }


}
