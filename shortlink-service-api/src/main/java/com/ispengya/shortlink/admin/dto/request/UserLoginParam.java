package com.ispengya.shortlink.admin.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/20 19:34
 */
@Data
public class UserLoginParam implements Serializable {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
