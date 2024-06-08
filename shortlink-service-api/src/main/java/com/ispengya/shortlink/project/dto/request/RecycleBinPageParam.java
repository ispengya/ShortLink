package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/12/9 16:36
 */
@Data
public class RecycleBinPageParam implements Serializable {
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
