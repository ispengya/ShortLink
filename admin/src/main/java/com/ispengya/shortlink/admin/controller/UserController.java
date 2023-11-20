package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.service.UserService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author ispengya
 * @date 2023/11/16 16:50
 */
@RestController
@RequestMapping("/shortlink/admin/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * 获取用户信息
     * @param username
     * @return
     */
    @GetMapping("userInfo/{username}")
    public Result<UserInfoRespDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userService.getUserByUserName(username);
        return Results.success(userInfoRespDTO);
    }

    /**
     * 注册用户
     * @return
     */
    @PostMapping("register")
    public Result<Void> register(@RequestBody @Valid UserRegisterReqDTO userRegisterReqDTO){
        userService.register(userRegisterReqDTO);
        return Results.success();
    }



}
