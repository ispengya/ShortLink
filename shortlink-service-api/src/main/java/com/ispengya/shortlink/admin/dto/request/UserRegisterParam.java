package com.ispengya.shortlink.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/16 18:19
 */
@Data
@Schema(description = "用户注册实体")
public class UserRegisterParam implements Serializable {
    /**
     * 用户名
     */
    @NotBlank
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     */
    @NotBlank
    @Schema(description = "密码")
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank
    @Schema(description = "手机号")
    private String phone;
}
