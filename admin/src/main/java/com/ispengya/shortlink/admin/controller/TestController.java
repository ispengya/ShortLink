package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ispengya
 * @date 2023/11/15 18:01
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Result test(){
        throw new RuntimeException("测试使用");
    }
}
