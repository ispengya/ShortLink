package com.ispengya.shortlink.admin.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/21 17:49
 */
@Data
public class GroupUpdateParam implements Serializable {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
