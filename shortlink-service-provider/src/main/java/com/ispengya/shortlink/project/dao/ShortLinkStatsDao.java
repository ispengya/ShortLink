package com.ispengya.shortlink.project.dao;

import com.ispengya.shortlink.project.domain.*;
import com.ispengya.shortlink.project.domain.dao.LinkStatsTodayDTO;
import com.ispengya.shortlink.project.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 短链接统计dao
 * @author: hanzhipeng
 * @create: 2024-12-22 11:59
 **/
@Repository
@RequiredArgsConstructor
public class ShortLinkStatsDao {

    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;


    /**
     * 查询today统计数据
     */
    public List<LinkStatsTodayDTO> selectListByShortUrls(List<String> shortUrls) {
        return linkStatsTodayMapper.selectListByShortUrl(shortUrls);
    }

    /**
     * 记录统计数据
     */
    public void shortLinkStats(LinkAccessStatsDO linkAccessStatsDO) {
        linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
    }

    /**
     * 记录位置统计数据
     */
    public void shortLinkLocaleState(LinkLocaleStatsDO linkLocaleStatsDO) {
        linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
    }

    /**
     * 记录os
     */
    public void shortLinkOsState(LinkOsStatsDO linkOsStatsDO) {
        linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
    }

    /**
     * 记录浏览器
     */
    public void shortLinkBrowserState(LinkBrowserStatsDO linkBrowserStatsDO) {
        linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
    }

    /**
     * 记录设备
     */
    public void shortLinkDeviceState(LinkDeviceStatsDO linkDeviceStatsDO) {
        linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
    }

    /**
     * 记录网络
     */
    public void shortLinkNetworkState(LinkNetworkStatsDO linkNetworkStatsDO) {
        linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);
    }

    /**
     * 记录访问日志
     */
    public void shortLinkAccessLogState(LinkAccessLogsDO linkAccessLogsDO) {
        linkAccessLogsMapper.insert(linkAccessLogsDO);
    }

    /**
     * 记录今日数据
     */
    public void shortLinkTodayState(LinkStatsTodayDO linkStatsTodayDO) {
        linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
    }


}

