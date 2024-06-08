package com.ispengya.shortlink.admin.controller.admin;

import com.ispengya.shortlink.admin.dto.request.UserLoginParam;
import com.ispengya.shortlink.admin.dto.request.UserRegisterParam;
import com.ispengya.shortlink.admin.dto.request.UserUpdateParam;
import com.ispengya.shortlink.admin.dto.response.UserInfoRespDTO;
import com.ispengya.shortlink.admin.dto.response.UserLoginRespDTO;
import com.ispengya.shortlink.admin.service.UserDubboService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ispengya
 * @date 2023/11/16 16:50
 */
@RestController
@RequestMapping("/api/short-link/admin")
public class UserController {

    @DubboReference
    private UserDubboService userDubboService;


    /**
     * 获取用户信息
     */
    @GetMapping("/auth/userInfo/{username}")
    public Result<UserInfoRespDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userDubboService.getUserByUserName(username);
        return Results.success(userInfoRespDTO);
    }

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterParam userRegisterParam) {
        userDubboService.register(userRegisterParam);
        return Results.success();
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/auth/admin/userInfo")
    public Result<Void> update(@RequestBody UserUpdateParam userUpdateParam) {
        userDubboService.updateUserInfo(userUpdateParam);
        return Results.success();
    }

    /**
     * 登录
     */
    @PostMapping("login")
    public Result<UserLoginRespDTO> login(@RequestBody @Valid UserLoginParam userLoginParam) {
        UserLoginRespDTO userLoginRespDTO = userDubboService.login(userLoginParam);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 登出
     */
    @GetMapping("logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userDubboService.logout(username, token);
        return Results.success();
    }

    /**
     * 判断用户是否登录
     */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin(@RequestParam("username") String username) {
        Boolean isLogin = userDubboService.checkLogin(username);
        return Results.success(isLogin);
    }


}
