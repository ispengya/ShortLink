package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShortLinkGroupStatsParam implements Serializable {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}