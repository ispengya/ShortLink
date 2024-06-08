package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/12/9 16:29
 */
@Data
public class RecycleSaveParam implements Serializable {
    /**
     * 全部短链接
     */
    private String fullShortUrl;

    /**
     * 用户名
     */
    private String username;
}
