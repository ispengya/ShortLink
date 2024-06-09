package com.ispengya.shortlink.project.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ispengya.shortlink.common.constant.MQConstant;
import static com.ispengya.shortlink.common.constant.RedisConstant.LOCK_GID_UPDATE_KEY;
import static com.ispengya.shortlink.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.JsonUtils;
import com.ispengya.shortlink.project.dao.core.ShortLinkDao;
import com.ispengya.shortlink.project.dao.core.ShortLinkGoToDao;
import com.ispengya.shortlink.project.domain.LinkAccessLogsDO;
import com.ispengya.shortlink.project.domain.LinkAccessStatsDO;
import com.ispengya.shortlink.project.domain.LinkBrowserStatsDO;
import com.ispengya.shortlink.project.domain.LinkDeviceStatsDO;
import com.ispengya.shortlink.project.domain.LinkLocaleStatsDO;
import com.ispengya.shortlink.project.domain.LinkNetworkStatsDO;
import com.ispengya.shortlink.project.domain.LinkOsStatsDO;
import com.ispengya.shortlink.project.domain.LinkStatsMQDTO;
import com.ispengya.shortlink.project.domain.LinkStatsTodayDO;
import com.ispengya.shortlink.project.domain.ShortLinkGotoDO;
import com.ispengya.shortlink.project.mapper.LinkAccessLogsMapper;
import com.ispengya.shortlink.project.mapper.LinkAccessStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkBrowserStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkDeviceStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkLocaleStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkNetworkStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkOsStatsMapper;
import com.ispengya.shortlink.project.mapper.LinkStatsTodayMapper;
import com.ispengya.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ispengya
 * @date 2023/12/10 17:15
 */
@Slf4j
@AllArgsConstructor
@Component
@RocketMQMessageListener(consumerGroup = MQConstant.SHORT_LINK_STATS_GROUP, topic = MQConstant.SHORT_LINK_STATS_TOPIC
        , messageModel = MessageModel.CLUSTERING)
public class LinkStatsConsumer implements RocketMQListener<LinkStatsMQDTO> {

    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    private final RedissonClient redissonClient;
    private final ShortLinkDao shortLinkDao;
    private final ShortLinkGoToDao shortLinkGoToDao;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;

    @Override
    public void onMessage(LinkStatsMQDTO linkStatsMQDTO) {
        log.info("短链接MQ统计数据：{}", JsonUtils.toStr(linkStatsMQDTO));
        String keys = linkStatsMQDTO.getKeys();
        if (messageQueueIdempotentHandler.isMessageBeingConsumed(keys)) {
            // 判断当前的这个消息流程是否执行完成
            if (messageQueueIdempotentHandler.isAccomplish(keys)) {
                return;
            }
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }
        try {
            actualSaveShortLinkStats(linkStatsMQDTO);
        } catch (Throwable ex) {
            // 某某某情况宕机了
            messageQueueIdempotentHandler.delMessageProcessed(keys);
            log.error("记录短链接监控消费异常", ex);
            throw ex;
        }
        messageQueueIdempotentHandler.setAccomplish(keys);
    }

    private void actualSaveShortLinkStats(LinkStatsMQDTO linkStatsMQDTO) {
        String fullShortUrl = linkStatsMQDTO.getFullShortUrl();
        //获取路由表username
        ShortLinkGotoDO shortLinkGotoDO = shortLinkGoToDao.getByFullShortUrlWithOut(fullShortUrl);
        if (Objects.isNull(shortLinkGotoDO)) {
            throw new ServiceException("统计失败");
        }
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            //更新总的统计数据
            shortLinkDao.incrementStats(shortLinkGotoDO.getUsername(),fullShortUrl, 1, linkStatsMQDTO.getUvFirstFlag() ? 1 : 0,
                    linkStatsMQDTO.getUipFirstFlag() ? 1 : 0);
            //保存uv、pv等统计数据
            Date currentDate = linkStatsMQDTO.getCurrentDate();
            int hour = DateUtil.hour(currentDate, true);
            Week week = DateUtil.dayOfWeekEnum(currentDate);
            int weekValue = week.getIso8601Value();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(linkStatsMQDTO.getUvFirstFlag() ? 1 : 0)
                    .uip(linkStatsMQDTO.getUipFirstFlag() ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            //保存位置统计数据
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", linkStatsMQDTO.getStatsLocaleAmapKey());
            localeParamMap.put("ip", linkStatsMQDTO.getRemoteAddr());
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "未知";
            String actualCity = "未知";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .province(actualProvince = unknownFlag ? actualProvince : province)
                        .city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .date(currentDate)
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .os(linkStatsMQDTO.getOs())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .browser(linkStatsMQDTO.getBrowser())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(linkStatsMQDTO.getDevice())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(linkStatsMQDTO.getNetwork())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .user(linkStatsMQDTO.getUv())
                    .ip(linkStatsMQDTO.getRemoteAddr())
                    .browser(linkStatsMQDTO.getBrowser())
                    .os(linkStatsMQDTO.getOs())
                    .network(linkStatsMQDTO.getNetwork())
                    .device(linkStatsMQDTO.getDevice())
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(linkStatsMQDTO.getUvFirstFlag() ? 1 : 0)
                    .todayUip(linkStatsMQDTO.getUipFirstFlag() ? 1 : 0)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        } finally {
            rLock.unlock();
        }
    }
}
