package com.ispengya.shortlink.admin.domain.dto.resp;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/16 16:55
 */
@Data
public class UserInfoDTO{
    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


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
