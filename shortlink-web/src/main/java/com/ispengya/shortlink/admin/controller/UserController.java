package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.domain.dto.req.UserLoginReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserLoginRespDTO;
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
@RequestMapping("/api/short-link/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * 获取用户信息
     */
    @GetMapping("/auth/userInfo/{username}")
    public Result<UserInfoRespDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userService.getUserByUserName(username);
        return Results.success(userInfoRespDTO);
    }

    /**
     * 注册用户
     */
    @PostMapping("/auth/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterReqDTO userRegisterReqDTO) {
        userService.register(userRegisterReqDTO);
        return Results.success();
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/auth/admin/userInfo")
    public Result<Void> update(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        userService.updateUserInfo(userUpdateReqDTO);
        return Results.success();
    }

    /**
     * 登录
     */
    @PostMapping("login")
    public Result<UserLoginRespDTO> login(@RequestBody @Valid UserLoginReqDTO userLoginReqDTO) {
        UserLoginRespDTO userLoginRespDTO = userService.login(userLoginReqDTO);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 登出
     */
    @GetMapping("logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }

    /**
     * 判断用户是否登录
     */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin(@RequestParam("username") String username) {
        Boolean isLogin = userService.checkLogin(username);
        return Results.success(isLogin);
    }


}
