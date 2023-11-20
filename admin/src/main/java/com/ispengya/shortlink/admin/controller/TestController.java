package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.common.util.AssertUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ispengya
 * @date 2023/11/15 18:01
 */
@RestController
public class TestController {

    @PostMapping("/test")
    public Result test( @Valid @RequestBody UserRegisterReqDTO userRegisterReqDTO){
        System.out.println(userRegisterReqDTO);
        AssertUtil.hasText(" ","dddddd");
        return Results.success();
    }
}
