package com.ispengya.shortlink.project.domain.dto.req;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/12/9 16:43
 */
@Data
public class RecycleBinRecoverReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
