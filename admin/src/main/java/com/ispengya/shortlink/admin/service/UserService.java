package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.admin.domain.dto.req.UserLoginReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserLoginRespDTO;

/**
* @author 86151
* @description 针对表【t_user】的数据库操作Service
* @createDate 2023-11-13 21:01:06
*/
public interface UserService{

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
     * @param userRegisterReqDTO
     */
    void register(UserRegisterReqDTO userRegisterReqDTO);

    /**
     * 修改用户信息
     * @param userUpdateReqDTO
     */
    void updateUserInfo(UserUpdateReqDTO userUpdateReqDTO);

    /**
     * 登录
     * @param userLoginReqDTO
     * @return
     */
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

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
