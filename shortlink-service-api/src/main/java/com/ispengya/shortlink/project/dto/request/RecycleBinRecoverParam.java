package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/12/9 16:43
 */
@Data
public class RecycleBinRecoverParam implements Serializable {
    /**
     * 用户名
     */
    private String username;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
