package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoDTO;

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
    UserInfoDTO getUserByUserName(String username);

    /**
     * 判断是否存在用户
     * @param username
     * @return
     */
    boolean hasUser(String username);
}
