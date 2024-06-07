package com.ispengya.shortlink.admin.domain.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/21 16:43
 */
@Data
public class GroupAddReqDTO {
    /**
     * 分组名
     */
    @NotBlank
    private String name;
}
