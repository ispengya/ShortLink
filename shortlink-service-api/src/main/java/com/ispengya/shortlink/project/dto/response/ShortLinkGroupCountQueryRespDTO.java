package com.ispengya.shortlink.project.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShortLinkGroupCountQueryRespDTO implements Serializable {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;
}