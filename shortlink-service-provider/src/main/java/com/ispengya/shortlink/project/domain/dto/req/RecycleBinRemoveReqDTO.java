package com.ispengya.shortlink.project.domain.dto.req;

import lombok.Data;

/**
 * @author ispengya
 * @date 2023/12/9 16:54
 */
@Data
public class RecycleBinRemoveReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
