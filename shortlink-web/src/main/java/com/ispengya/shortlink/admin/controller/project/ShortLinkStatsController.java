package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkStatsAccessRecordRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkStatsRespDTO;
import com.ispengya.shortlink.project.service.ShortLinkDubboService;
import com.ispengya.shortlink.project.service.ShortLinkStatsDubboService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    @DubboReference
    private ShortLinkStatsDubboService shortLinkStatsDubboService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsParam requestParam) {
        return Results.success(shortLinkStatsDubboService.oneShortLinkStats(requestParam));
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsParam requestParam) {
        return Results.success(shortLinkStatsDubboService.groupShortLinkStats(requestParam));
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<PageDTO<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordParam requestParam) {
        return Results.success(shortLinkStatsDubboService.shortLinkStatsAccessRecord(requestParam));
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    public Result<PageDTO<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordParam requestParam) {
        return Results.success(shortLinkStatsDubboService.groupShortLinkStatsAccessRecord(requestParam));
    }
}