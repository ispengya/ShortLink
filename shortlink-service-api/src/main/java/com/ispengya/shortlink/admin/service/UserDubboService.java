package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.admin.dto.request.UserLoginParam;
import com.ispengya.shortlink.admin.dto.request.UserRegisterParam;
import com.ispengya.shortlink.admin.dto.request.UserUpdateParam;
import com.ispengya.shortlink.admin.dto.response.UserInfoRespDTO;
import com.ispengya.shortlink.admin.dto.response.UserLoginRespDTO;

public interface UserDubboService {

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    UserInfoRespDTO getUserByUserName(String username);

    /**
     * 判断是否存在用户名
     * @param username
     * @return
     */
    boolean hasUserName(String username);

    /**
     * 用户注册
     * @param userRegisterParam
     */
    void register(UserRegisterParam userRegisterParam);

    /**
     * 修改用户信息
     * @param userUpdateParam
     */
    void updateUserInfo(UserUpdateParam userUpdateParam);

    /**
     * 登录
     * @param userLoginParam
     * @return
     */
    UserLoginRespDTO login(UserLoginParam userLoginParam);

    /**
     * 登出
     * @param username
     * @param token
     */
    void logout(String username, String token);

    /**
     * 检测是否登录
     * @param username
     * @return
     */
    Boolean checkLogin(String username);
}