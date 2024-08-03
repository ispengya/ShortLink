package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.service.TestService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ispengya
 * @date 2023/11/15 18:01
 */
@RestController
public class TestController {
    @DubboReference
    private TestService testService;
    @GetMapping("/test")
    public String test(){
        testService.hello();
        return "ok";
    }
}
