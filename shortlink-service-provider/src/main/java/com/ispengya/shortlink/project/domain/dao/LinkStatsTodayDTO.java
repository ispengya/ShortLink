package com.ispengya.shortlink.project.domain.dao;

import lombok.Data;

/**
 * @description:
 * @author: hanzhipeng
 * @create: 2024-12-22 13:23
 **/
@Data
public class LinkStatsTodayDTO {

    private String shortUrl;
    /**
     * 今日pv
     */
    private Integer todayPv;

    /**
     * 今日uv
     */
    private Integer todayUv;

    /**
     * 今日ip数
     */
    private Integer todayUip;

}
