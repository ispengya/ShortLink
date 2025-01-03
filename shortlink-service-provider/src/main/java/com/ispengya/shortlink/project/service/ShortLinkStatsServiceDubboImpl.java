package com.ispengya.shortlink.project.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ispengya.shortlink.admin.domain.Group;
import com.ispengya.shortlink.common.biz.user.UserContext;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.project.domain.*;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsAccessRecordParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import com.ispengya.shortlink.project.dto.response.*;
import com.ispengya.shortlink.project.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 短链接监控接口实现层
 */
@Service
@DubboService
@Slf4j
public class ShortLinkStatsServiceDubboImpl implements ShortLinkStatsDubboService {

    @Autowired
    private LinkGroupMapper linkGroupMapper;
    @Autowired
    private LinkAccessStatsMapper linkAccessStatsMapper;
    @Autowired
    private LinkLocaleStatsMapper linkLocaleStatsMapper;
    @Autowired
    private LinkAccessLogsMapper linkAccessLogsMapper;
    @Autowired
    private LinkBrowserStatsMapper linkBrowserStatsMapper;
    @Autowired
    private LinkOsStatsMapper linkOsStatsMapper;
    @Autowired
    private LinkDeviceStatsMapper linkDeviceStatsMapper;
    @Autowired
    private LinkNetworkStatsMapper linkNetworkStatsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsParam requestParam) {
        checkGroupBelongToUser(requestParam.getGid());
        requestParam.setUsername(UserContext.getUsername());
        List<LinkAccessStatsDO> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(requestParam);
        if (CollUtil.isEmpty(listStatsByShortLink)) {
            return null;
        }
        //返回数据
        ShortLinkStatsRespDTO shortLinkStatsRespDTO = new ShortLinkStatsRespDTO();
        //访问日志数据
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            LinkAccessStatsDO pvUvUidStatsByShortLink = linkAccessLogsMapper.findPvUvUidStatsByShortLink(requestParam);
            shortLinkStatsRespDTO.setPv(pvUvUidStatsByShortLink.getPv());
            shortLinkStatsRespDTO.setUv(pvUvUidStatsByShortLink.getUv());
            shortLinkStatsRespDTO.setUip(pvUvUidStatsByShortLink.getUip());
        });
        // 基础访问详情
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
            List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
                    .map(DateUtil::formatDate)
                    .collect(Collectors.toList());
            rangeDates.forEach(each -> {
                Optional<LinkAccessStatsDO> optional = listStatsByShortLink.stream()
                        .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate())))
                        .findFirst();
                if (optional.isPresent()) {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                            .date(each)
                            .pv(optional.get().getPv())
                            .uv(optional.get().getUv())
                            .uip(optional.get().getUip())
                            .build();
                    daily.add(accessDailyRespDTO);
                } else {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                            .date(each)
                            .pv(0)
                            .uv(0)
                            .uip(0)
                            .build();
                    daily.add(accessDailyRespDTO);
                }
            });
            shortLinkStatsRespDTO.setDaily(daily);
        });
        // 地区访问详情（仅国内）
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
            List<LinkLocaleStatsDO> listedLocaleByShortLink = linkLocaleStatsMapper.listLocaleByShortLink(requestParam);
            int localeCnSum = listedLocaleByShortLink.stream()
                    .mapToInt(LinkLocaleStatsDO::getCnt)
                    .sum();
            listedLocaleByShortLink.forEach(each -> {
                double ratio = (double) each.getCnt() / localeCnSum;
                double actualRatio = Math.round(ratio * 100.0) / 100.0;
                ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                        .cnt(each.getCnt())
                        .locale(each.getProvince())
                        .ratio(actualRatio)
                        .build();
                localeCnStats.add(localeCNRespDTO);
            });
            shortLinkStatsRespDTO.setLocaleCnStats(localeCnStats);
        });
        // 小时访问详情
        CompletableFuture<Void> completableFuture4 = CompletableFuture.runAsync(() -> {
            List<Integer> hourStats = new ArrayList<>();
            List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
            for (int i = 0; i < 24; i++) {
                AtomicInteger hour = new AtomicInteger(i);
                int hourCnt = listHourStatsByShortLink.stream()
                        .filter(each -> Objects.equals(each.getHour(), hour.get()))
                        .findFirst()
                        .map(LinkAccessStatsDO::getPv)
                        .orElse(0);
                hourStats.add(hourCnt);
            }
            shortLinkStatsRespDTO.setHourStats(hourStats);
        });
        // 高频访问IP详情
        CompletableFuture<Void> completableFuture5 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
            List<HashMap<String, Object>> listTopIpByShortLink = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
            listTopIpByShortLink.forEach(each -> {
                ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                        .ip(each.get("ip").toString())
                        .cnt(Integer.parseInt(each.get("count").toString()))
                        .build();
                topIpStats.add(statsTopIpRespDTO);
            });
            shortLinkStatsRespDTO.setTopIpStats(topIpStats);
        });
        // 一周访问详情
        CompletableFuture<Void> completableFuture6 = CompletableFuture.runAsync(() -> {
            List<Integer> weekdayStats = new ArrayList<>();
            List<LinkAccessStatsDO> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(requestParam);
            for (int i = 1; i < 8; i++) {
                AtomicInteger weekday = new AtomicInteger(i);
                int weekdayCnt = listWeekdayStatsByShortLink.stream()
                        .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                        .findFirst()
                        .map(LinkAccessStatsDO::getPv)
                        .orElse(0);
                weekdayStats.add(weekdayCnt);
            }
            shortLinkStatsRespDTO.setWeekdayStats(weekdayStats);
        });
        // 浏览器访问详情
        CompletableFuture<Void> completableFuture7 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
            List<HashMap<String, Object>> listBrowserStatsByShortLink = linkBrowserStatsMapper.listBrowserStatsByShortLink(requestParam);
            int browserSum = listBrowserStatsByShortLink.stream()
                    .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                    .sum();
            listBrowserStatsByShortLink.forEach(each -> {
                double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
                double actualRatio = Math.round(ratio * 100.0) / 100.0;
                ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                        .cnt(Integer.parseInt(each.get("count").toString()))
                        .browser(each.get("browser").toString())
                        .ratio(actualRatio)
                        .build();
                browserStats.add(browserRespDTO);
            });
            shortLinkStatsRespDTO.setBrowserStats(browserStats);
        });
        // 操作系统访问详情
        CompletableFuture<Void> completableFuture8 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
            List<HashMap<String, Object>> listOsStatsByShortLink = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
            int osSum = listOsStatsByShortLink.stream()
                    .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                    .sum();
            listOsStatsByShortLink.forEach(each -> {
                double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
                double actualRatio = Math.round(ratio * 100.0) / 100.0;
                ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                        .cnt(Integer.parseInt(each.get("count").toString()))
                        .os(each.get("os").toString())
                        .ratio(actualRatio)
                        .build();
                osStats.add(osRespDTO);
            });
            shortLinkStatsRespDTO.setOsStats(osStats);
        });
        // 访客访问类型详情
        CompletableFuture<Void> completableFuture9 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
            HashMap<String, Object> findUvTypeByShortLink = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
            int oldUserCnt = Integer.parseInt(
                    Optional.ofNullable(findUvTypeByShortLink)
                            .map(each -> each.get("oldUserCnt"))
                            .map(Object::toString)
                            .orElse("0")
            );
            int newUserCnt = Integer.parseInt(
                    Optional.ofNullable(findUvTypeByShortLink)
                            .map(each -> each.get("newUserCnt"))
                            .map(Object::toString)
                            .orElse("0")
            );
            int uvSum = oldUserCnt + newUserCnt;
            double oldRatio = (double) oldUserCnt / uvSum;
            double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
            double newRatio = (double) newUserCnt / uvSum;
            double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;
            ShortLinkStatsUvRespDTO newUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                    .uvType("newUser")
                    .cnt(newUserCnt)
                    .ratio(actualNewRatio)
                    .build();
            uvTypeStats.add(newUvRespDTO);
            ShortLinkStatsUvRespDTO oldUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                    .uvType("oldUser")
                    .cnt(oldUserCnt)
                    .ratio(actualOldRatio)
                    .build();
            uvTypeStats.add(oldUvRespDTO);
            shortLinkStatsRespDTO.setUvTypeStats(uvTypeStats);
        });
        // 访问设备类型详情
        CompletableFuture<Void> completableFuture10 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
            List<LinkDeviceStatsDO> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
            int deviceSum = listDeviceStatsByShortLink.stream()
                    .mapToInt(LinkDeviceStatsDO::getCnt)
                    .sum();
            listDeviceStatsByShortLink.forEach(each -> {
                double ratio = (double) each.getCnt() / deviceSum;
                double actualRatio = Math.round(ratio * 100.0) / 100.0;
                ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                        .cnt(each.getCnt())
                        .device(each.getDevice())
                        .ratio(actualRatio)
                        .build();
                deviceStats.add(deviceRespDTO);
            });
            shortLinkStatsRespDTO.setDeviceStats(deviceStats);
        });
        // 访问网络类型详情
        CompletableFuture<Void> completableFuture11 = CompletableFuture.runAsync(() -> {
            List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
            List<LinkNetworkStatsDO> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(requestParam);
            int networkSum = listNetworkStatsByShortLink.stream()
                    .mapToInt(LinkNetworkStatsDO::getCnt)
                    .sum();
            listNetworkStatsByShortLink.forEach(each -> {
                double ratio = (double) each.getCnt() / networkSum;
                double actualRatio = Math.round(ratio * 100.0) / 100.0;
                ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                        .cnt(each.getCnt())
                        .network(each.getNetwork())
                        .ratio(actualRatio)
                        .build();
                networkStats.add(networkRespDTO);
            });
            shortLinkStatsRespDTO.setNetworkStats(networkStats);
        });
        try {
            CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3, completableFuture4, completableFuture5,
                    completableFuture6, completableFuture7, completableFuture8, completableFuture9, completableFuture10, completableFuture11).get();
        } catch (Exception e) {
            throw new ServiceException("查询失败");
        }
        return shortLinkStatsRespDTO;
    }

    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsParam requestParam) {
        checkGroupBelongToUser(requestParam.getGid());
        List<LinkAccessStatsDO> listStatsByGroup = linkAccessStatsMapper.listStatsByGroup(requestParam);
        if (CollUtil.isEmpty(listStatsByGroup)) {
            return null;
        }
        // 基础访问数据
        LinkAccessStatsDO pvUvUidStatsByGroup = linkAccessLogsMapper.findPvUvUidStatsByGroup(requestParam);
        // 基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
                .map(DateUtil::formatDate)
                .collect(Collectors.toList());
        rangeDates.forEach(each -> {
            Optional<LinkAccessStatsDO> matchingItem = listStatsByGroup.stream()
                    .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate())))
                    .findFirst();
            if (matchingItem.isPresent()) {
                ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                        .date(each)
                        .pv(matchingItem.get().getPv())
                        .uv(matchingItem.get().getUv())
                        .uip(matchingItem.get().getUip())
                        .build();
                daily.add(accessDailyRespDTO);
            } else {
                ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                        .date(each)
                        .pv(0)
                        .uv(0)
                        .uip(0)
                        .build();
                daily.add(accessDailyRespDTO);
            }
        });
        // 地区访问详情（仅国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
        List<LinkLocaleStatsDO> listedLocaleByGroup = linkLocaleStatsMapper.listLocaleByGroup(requestParam);
        int localeCnSum = listedLocaleByGroup.stream()
                .mapToInt(LinkLocaleStatsDO::getCnt)
                .sum();
        listedLocaleByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / localeCnSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(each.getCnt())
                    .locale(each.getProvince())
                    .ratio(actualRatio)
                    .build();
            localeCnStats.add(localeCNRespDTO);
        });
        // 小时访问详情
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStatsDO> listHourStatsByGroup = linkAccessStatsMapper.listHourStatsByGroup(requestParam);
        for (int i = 0; i < 24; i++) {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = listHourStatsByGroup.stream()
                    .filter(each -> Objects.equals(each.getHour(), hour.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }
        // 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByGroup = linkAccessLogsMapper.listTopIpByGroup(requestParam);
        listTopIpByGroup.forEach(each -> {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            topIpStats.add(statsTopIpRespDTO);
        });
        // 一周访问详情
        List<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStatsDO> listWeekdayStatsByGroup = linkAccessStatsMapper.listWeekdayStatsByGroup(requestParam);
        for (int i = 1; i < 8; i++) {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = listWeekdayStatsByGroup.stream()
                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        // 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByGroup = linkBrowserStatsMapper.listBrowserStatsByGroup(requestParam);
        int browserSum = listBrowserStatsByGroup.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listBrowserStatsByGroup.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .browser(each.get("browser").toString())
                    .ratio(actualRatio)
                    .build();
            browserStats.add(browserRespDTO);
        });
        // 操作系统访问详情
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByGroup = linkOsStatsMapper.listOsStatsByGroup(requestParam);
        int osSum = listOsStatsByGroup.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listOsStatsByGroup.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .os(each.get("os").toString())
                    .ratio(actualRatio)
                    .build();
            osStats.add(osRespDTO);
        });
        // 访问设备类型详情
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<LinkDeviceStatsDO> listDeviceStatsByGroup = linkDeviceStatsMapper.listDeviceStatsByGroup(requestParam);
        int deviceSum = listDeviceStatsByGroup.stream()
                .mapToInt(LinkDeviceStatsDO::getCnt)
                .sum();
        listDeviceStatsByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / deviceSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .cnt(each.getCnt())
                    .device(each.getDevice())
                    .ratio(actualRatio)
                    .build();
            deviceStats.add(deviceRespDTO);
        });
        // 访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<LinkNetworkStatsDO> listNetworkStatsByGroup = linkNetworkStatsMapper.listNetworkStatsByGroup(requestParam);
        int networkSum = listNetworkStatsByGroup.stream()
                .mapToInt(LinkNetworkStatsDO::getCnt)
                .sum();
        listNetworkStatsByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .cnt(each.getCnt())
                    .network(each.getNetwork())
                    .ratio(actualRatio)
                    .build();
            networkStats.add(networkRespDTO);
        });
        return ShortLinkStatsRespDTO.builder()
                .pv(pvUvUidStatsByGroup.getPv())
                .uv(pvUvUidStatsByGroup.getUv())
                .uip(pvUvUidStatsByGroup.getUip())
                .daily(daily)
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .build();
    }

    @Override
    public PageDTO<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordParam requestParam) {
        checkGroupBelongToUser(requestParam.getGid());
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, requestParam.getFullShortUrl())
                .between(LinkAccessLogsDO::getCreateTime, requestParam.getStartDate(), requestParam.getEndDate())
                .eq(LinkAccessLogsDO::getDelFlag, 0)
                .orderByDesc(LinkAccessLogsDO::getCreateTime);
        Page<LinkAccessLogsDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        IPage<LinkAccessLogsDO> linkAccessLogsDOIPage = linkAccessLogsMapper.selectPage(page, queryWrapper);
        if (CollUtil.isEmpty(linkAccessLogsDOIPage.getRecords())) {
            return new PageDTO<>();
        }
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsDOIPage.convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .collect(Collectors.toList());
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getUsername(),
                requestParam.getFullShortUrl(),
                requestParam.getEnableStatus(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        actualResult.getRecords().forEach(each -> {
            String uvType = uvTypeList.stream()
                    .filter(item -> Objects.equals(each.getUser(), item.get("user")))
                    .findFirst()
                    .map(item -> item.get("uvType"))
                    .map(Object::toString)
                    .orElse("旧访客");
            each.setUvType(uvType);
        });
        PageDTO<ShortLinkStatsAccessRecordRespDTO> pageDTO = new PageDTO<>();
        pageDTO.setRecords(actualResult.getRecords());
        pageDTO.setTotal(actualResult.getTotal());
        pageDTO.setCurrent(actualResult.getCurrent());
        pageDTO.setSize(actualResult.getSize());
        pageDTO.setPages(actualResult.getPages());
        return pageDTO;
    }

    @Override
    public PageDTO<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(
            ShortLinkGroupStatsAccessRecordParam requestParam) {
        checkGroupBelongToUser(requestParam.getGid());
        IPage<LinkAccessLogsDO> linkAccessLogsDOIPage = linkAccessLogsMapper.selectGroupPage(requestParam);
        if (CollUtil.isEmpty(linkAccessLogsDOIPage.getRecords())) {
            return new PageDTO<>();
        }
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsDOIPage
                .convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .collect(Collectors.toList());
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectGroupUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        actualResult.getRecords().forEach(each -> {
            String uvType = uvTypeList.stream()
                    .filter(item -> Objects.equals(each.getUser(), item.get("user")))
                    .findFirst()
                    .map(item -> item.get("uvType"))
                    .map(Object::toString)
                    .orElse("旧访客");
            each.setUvType(uvType);
        });
        PageDTO<ShortLinkStatsAccessRecordRespDTO> pageDTO = new PageDTO<>();
        pageDTO.setRecords(actualResult.getRecords());
        pageDTO.setTotal(actualResult.getTotal());
        pageDTO.setCurrent(actualResult.getCurrent());
        pageDTO.setSize(actualResult.getSize());
        pageDTO.setPages(actualResult.getPages());
        return pageDTO;
    }

    public void checkGroupBelongToUser(String gid) throws ServiceException {
        String username = Optional.ofNullable(UserContext.getUsername())
                .orElseThrow(() -> new ServiceException("用户未登录"));
        LambdaQueryWrapper<Group> queryWrapper = Wrappers.lambdaQuery(Group.class)
                .eq(Group::getGid, gid)
                .eq(Group::getUsername, username);
        List<Group> groupDOList = linkGroupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户信息与分组标识不匹配");
        }
    }
}
