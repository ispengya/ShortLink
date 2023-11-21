package com.ispengya.shortlink.admin.domain.dto.resp;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/11/21 17:21
 */
@Data
public class GroupListRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;
}
