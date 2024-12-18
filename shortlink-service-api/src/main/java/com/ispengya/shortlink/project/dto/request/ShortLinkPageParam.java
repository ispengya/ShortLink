package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/25 17:42
 */
@Data
public class ShortLinkPageParam implements Serializable {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 排序标识
     */
    private String orderTag;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 页大小
     */
    private Long size;
}
