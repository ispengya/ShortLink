package com.ispengya.shortlink.admin.domain.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/20 19:34
 */
@Data
public class UserLoginReqDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
