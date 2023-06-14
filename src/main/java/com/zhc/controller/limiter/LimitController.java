package com.zhc.controller.limiter;

import com.zhc.core.annotation.Limiter;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author zhouhengchao
 * @since 2023-06-13 20:49:00
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/zhc/java/limiter")
public class LimitController {

    @GetMapping("/guavaLimiter")
    @Limiter(limitNum = 6,name = "LimitController")
    public String generateException(){
        String retStr = "接口正常执行";
        System.out.println(retStr);
        return retStr;
    }
}
