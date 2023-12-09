package com.ispengya.shortlink.project.domain.dto.req;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/12/9 16:36
 */
@Data
public class RecycleBinPageReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 页大小
     */
    private Long pageSize;
}
