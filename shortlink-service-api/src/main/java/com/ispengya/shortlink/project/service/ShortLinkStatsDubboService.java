package com.ispengya.shortlink.project.service;


import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkStatsAccessRecordRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsDubboService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsParam requestParam);

    /**
     * 获取分组短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsParam requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取短链接监控访问记录数据入参
     * @return 访问记录监控数据
     */
    PageDTO<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordParam requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     *
     * @param requestParam 获取分组短链接监控访问记录数据入参
     * @return 分组访问记录监控数据
     */
    PageDTO<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordParam requestParam);
}
