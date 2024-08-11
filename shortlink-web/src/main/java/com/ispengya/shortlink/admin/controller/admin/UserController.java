package com.ispengya.shortlink.admin.controller.admin;

import com.ispengya.shortlink.admin.dto.request.UserLoginParam;
import com.ispengya.shortlink.admin.dto.request.UserRegisterParam;
import com.ispengya.shortlink.admin.dto.request.UserUpdateParam;
import com.ispengya.shortlink.admin.dto.response.UserInfoRespDTO;
import com.ispengya.shortlink.admin.dto.response.UserLoginRespDTO;
import com.ispengya.shortlink.admin.service.UserDubboService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "链易短-用户接口")
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class UserController {

    @DubboReference
    private UserDubboService userDubboService;


    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/actual/user/{username}")
    public Result<UserInfoRespDTO> getUserByName(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userDubboService.getUserByUserName(username);
        return Results.success(userInfoRespDTO);
    }

    /**
     * 注册用户
     */
    @Operation(summary = "注册用户")
    @PostMapping("/user")
    public Result<Void> register(@RequestBody @Valid UserRegisterParam userRegisterParam) {
        userDubboService.register(userRegisterParam);
        return Results.success();
    }

    /**
     * 修改个人信息
     */
    @Operation(summary = "修改个人信息")
    @PutMapping("/user")
    public Result<Void> update(@RequestBody UserUpdateParam userUpdateParam) {
        userDubboService.updateUserInfo(userUpdateParam);
        return Results.success();
    }

    /**
     * 登录
     */
    @Operation(summary = "登录")
    @PostMapping("/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody @Valid UserLoginParam userLoginParam) {
        UserLoginRespDTO userLoginRespDTO = userDubboService.login(userLoginParam);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 登出
     */
    @Operation(summary = "登出")
    @GetMapping("/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userDubboService.logout(username, token);
        return Results.success();
    }

    /**
     * 判断用户是否登录
     */
    @Operation(summary = "是否登录")
    @GetMapping("/user/has-username")
    public Result<Boolean> isLogin(@RequestParam("username") String username) {
        Boolean isLogin = userDubboService.checkLogin(username);
        return Results.success(isLogin);
    }


}
