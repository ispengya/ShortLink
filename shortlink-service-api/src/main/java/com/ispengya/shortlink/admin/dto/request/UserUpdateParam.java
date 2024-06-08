package com.ispengya.shortlink.admin.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/20 18:16
 */
@Data
public class UserUpdateParam implements Serializable {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

}
