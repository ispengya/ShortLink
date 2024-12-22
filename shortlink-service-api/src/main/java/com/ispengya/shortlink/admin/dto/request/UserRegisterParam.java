package com.ispengya.shortlink.admin.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/16 18:19
 */
@Data
public class UserRegisterParam implements Serializable {
    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank
    private String realName;

    /**
     * 手机号
     */
    @NotBlank
    private String phone;
}
