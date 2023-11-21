package com.ispengya.shortlink.admin.domain.dto.req;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/21 17:49
 */
@Data
public class GroupUpdateReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
