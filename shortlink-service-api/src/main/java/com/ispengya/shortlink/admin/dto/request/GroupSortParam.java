package com.ispengya.shortlink.admin.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/21 17:37
 */
@Data
public class GroupSortParam implements Serializable {

    /**
     * 分组ID
     */
    private String gid;

    /**
     * 排序
     */
    private Integer sortOrder;
}
