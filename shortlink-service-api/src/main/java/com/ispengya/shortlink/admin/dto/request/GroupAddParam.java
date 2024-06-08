package com.ispengya.shortlink.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/21 16:43
 */
@Data
public class GroupAddParam implements Serializable {
    /**
     * 分组名
     */
    @NotBlank
    private String name;
}
