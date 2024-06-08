package com.ispengya.shortlink.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/20 19:38
 */
@Data
@AllArgsConstructor
public class UserLoginRespDTO implements Serializable {
    private String token;
}
