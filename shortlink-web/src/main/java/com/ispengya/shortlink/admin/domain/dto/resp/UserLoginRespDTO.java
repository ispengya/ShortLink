package com.ispengya.shortlink.admin.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/20 19:38
 */
@Data
@AllArgsConstructor
public class UserLoginRespDTO {
    private String token;
}
