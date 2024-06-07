package com.ispengya.shortlink.admin.domain.dto.req;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/21 17:37
 */
@Data
public class GroupSortReqDTO {

    /**
     * 分组ID
     */
    private String gid;

    /**
     * 排序
     */
    private Integer sortOrder;
}
