package com.ispengya.shortlink.admin.controller.admin;

import com.ispengya.shortlink.admin.dto.request.UserLoginParam;
import com.ispengya.shortlink.admin.dto.request.UserRegisterParam;
import com.ispengya.shortlink.admin.dto.request.UserUpdateParam;
import com.ispengya.shortlink.admin.dto.response.UserInfoRespDTO;
import com.ispengya.shortlink.admin.dto.response.UserLoginRespDTO;
import com.ispengya.shortlink.admin.service.UserDubboService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ispengya
 * @date 2023/11/16 16:50
 */
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @DubboReference(retries = 0)
    private UserDubboService userDubboService;


    /**
     * 获取用户信息
     */
    @GetMapping("/actual/user/{username}")
    public Result<UserInfoRespDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userDubboService.getUserByUserName(username);
        return Results.success(userInfoRespDTO);
    }

    /**
     * 注册用户
     */
    @PostMapping("/user")
    public Result<Void> register(@RequestBody @Valid UserRegisterParam userRegisterParam) {
        userDubboService.register(userRegisterParam);
        return Results.success();
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/user")
    public Result<Void> update(@RequestBody UserUpdateParam userUpdateParam) {
        userDubboService.updateUserInfo(userUpdateParam);
        return Results.success();
    }

    /**
     * 登录
     */
    @PostMapping("/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody @Valid UserLoginParam userLoginParam) {
        UserLoginRespDTO userLoginRespDTO = userDubboService.login(userLoginParam);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 登出
     */
    @GetMapping("/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userDubboService.logout(username, token);
        return Results.success();
    }

    /**
     * 判断用户是否登录
     */
    @GetMapping("/user/has-username")
    public Result<Boolean> isLogin(@RequestParam("username") String username) {
        Boolean isLogin = userDubboService.checkLogin(username);
        return Results.success(isLogin);
    }


}
