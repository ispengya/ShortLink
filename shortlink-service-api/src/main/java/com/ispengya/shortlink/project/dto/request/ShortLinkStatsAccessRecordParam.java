package com.ispengya.shortlink.project.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShortLinkStatsAccessRecordParam implements Serializable {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

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

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus = 0;

    private Long current;

    private Long size;

    private String username;
}