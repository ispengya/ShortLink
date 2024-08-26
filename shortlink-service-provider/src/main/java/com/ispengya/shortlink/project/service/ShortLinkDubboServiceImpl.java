package com.ispengya.shortlink.project.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ispengya.shortlink.common.biz.UserContext;
import com.ispengya.shortlink.common.constant.RedisConstant;

import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_IS_NULL_PRE_KEY;
import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_PRE_KEY;
import static com.ispengya.shortlink.common.constant.RedisConstant.LOCK_GET_ORIGIN_LINK_PRE_KEY;

import com.ispengya.shortlink.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.common.converter.BeanConverter;
import com.ispengya.shortlink.common.converter.ShortLinkConverter;
import com.ispengya.shortlink.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.dao.core.ShortLinkDao;
import com.ispengya.shortlink.project.dao.core.ShortLinkGoToDao;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.domain.ShortLinkGotoDO;
import com.ispengya.shortlink.project.dto.request.ShortLinkBatchCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.*;
import com.ispengya.shortlink.project.mq.producer.LinkStatsProducer;
import com.ispengya.shortlink.project.util.HashUtil;
import com.ispengya.shortlink.project.util.LinkUtil;
import com.ispengya.travel.frameworks.starter.cache.core.multistage.MultiStageCache;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ispengya
 * @date 2023/11/25 14:20
 */
@Service
@DubboService
@RequiredArgsConstructor
@Slf4j
public class ShortLinkDubboServiceImpl implements ShortLinkDubboService {

    @Value("${short-link.official.domain}")
    private String officialDomain;
    private final ShortLinkDao shortLinkDao;
    private final ShortLinkGoToDao shortLinkGoToDao;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final UrlDubboServiceImpl urlService;
    private final RocketMQTemplate rocketMQTemplate;
    private final MultiStageCache multiStageCache;
    private final LinkStatsProducer linkStatsProducer;

    @Override
    public void jumpUrlV1(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        String originLink = multiStageCache.getByMultiStageCacheWithoutLimit(LINK_GOTO_PRE_KEY + fullShortUrl,
                String.class, null);
        if (StrUtil.isEmpty(originLink)) {
            RLock lock = redissonClient.getLock(LOCK_GET_ORIGIN_LINK_PRE_KEY + fullShortUrl);
            lock.lock();
            try {
                //双重检测
                originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
                if (StrUtil.isBlank(originLink)) {
                    //获取路由表
                    String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_IS_NULL_PRE_KEY+
                            fullShortUrl);
                    if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }
                    ShortLinkGotoDO shortLinkGotoDO = shortLinkGoToDao.getByFullShortUrl(fullShortUrl);
                    if (shortLinkGotoDO == null) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null");
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }
                    ShortLinkDO shortLinkDO = shortLinkDao.getOneByConditions(shortLinkGotoDO.getUsername(), shortLinkGotoDO.getFullShortUrl());
                    if (shortLinkDO == null || (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDateType().equals(ValidTypeEnum.CUSTOM.getType()) && shortLinkDO.getValidDate().before(new Date()))) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null", 30, TimeUnit.MINUTES);
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }
                    //判断有效期是否永久
                    if (!Objects.equals(shortLinkDO.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkDO.getOriginUrl(),
                                LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
                        );
                    } else {
                        //在缓存过期默认一个月
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkDO.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
                    }
                    originLink = shortLinkDO.getOriginUrl();
                }
            } finally {
                lock.unlock();
            }
        }
        //进行重定向
        if (StrUtil.isNotBlank(originLink)) {
            //异步统计
            linkStatsProducer.sendMsg(fullShortUrl, request, response);
            ((HttpServletResponse) response).sendRedirect(originLink);
        } else {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
        }
    }


    @Override
    @SneakyThrows
    public void jumpUrlV2(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        String originLink = "";
        //是否热key
        Object hotKeyValue = JdHotKeyStore.getValue(LINK_GOTO_PRE_KEY + fullShortUrl);
        if (Objects.nonNull(hotKeyValue)) {
            //是热key并且有值
            originLink = String.valueOf(hotKeyValue);
        } else {
            //不是热key，为null 。为热key但是未设置值
            originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
            if (StrUtil.isNotBlank(originLink)) {
                setHotKeyOrNull(LINK_GOTO_PRE_KEY + fullShortUrl, originLink);
            } else {
                //redis也没有,去数据库加载
                //解决缓存穿透,先用布隆过滤器判断
                boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
                if (!contains) {
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    return;
                }
                //判断空值,其实大部分布隆过滤器就可以解决，但还是会存在漏判
                String isNull = stringRedisTemplate.opsForValue().get(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl);
                if (StrUtil.isNotBlank(isNull)) {//说明缓存的空值,防止穿透
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    return;
                }
                RLock lock = redissonClient.getLock(LOCK_GET_ORIGIN_LINK_PRE_KEY + fullShortUrl);
                lock.lock();
                try {
                    //双重检测
                    originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
                    if (StrUtil.isNotBlank(originLink)) {
                        setHotKeyOrNull(LINK_GOTO_PRE_KEY + fullShortUrl, originLink);
                    } else {
                        ShortLinkGotoDO shortLinkGotoDO = shortLinkGoToDao.getByFullShortUrl(fullShortUrl);
                        if (shortLinkGotoDO == null) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null");
                            ((HttpServletResponse) response).sendRedirect("/page/notfound");
                            return;
                        }
                        ShortLinkDO
                                shortLinkDO = shortLinkDao.getOneByConditions(shortLinkGotoDO.getUsername(), shortLinkGotoDO.getFullShortUrl());
                        if (shortLinkDO == null || (shortLinkDO.getValidDateType().equals(ValidTypeEnum.CUSTOM.getType()) && shortLinkDO.getValidDate().before(new Date()))) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null", 30, TimeUnit.MINUTES);
                            ((HttpServletResponse) response).sendRedirect("/page/notfound");
                            return;
                        }
                        //判断有效期是否永久
                        if (!Objects.equals(shortLinkDO.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkDO.getOriginUrl(),
                                    LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
                            );
                            originLink = shortLinkDO.getOriginUrl();
                        } else {
                            originLink = shortLinkDO.getOriginUrl();
                            //在缓存过期默认一个月
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkDO.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
                            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
                        }
                        setHotKeyOrNull(LINK_GOTO_PRE_KEY + fullShortUrl, originLink);
                    }
                } finally {
                    lock.unlock();
                }
            }

        }
    }

    @Override
    public ShortLinkCreateRespDTO createLink(ShortLinkCreateParam shortLinkCreateParam) {
        //生成短链接uri
        String shortUri = generateSuffix(shortLinkCreateParam);
        String fullShortUrl = StrBuilder.create(shortLinkCreateParam.getDomain())
                .append("/")
                .append(shortUri)
                .toString();
        //获取favicon
        String favicon = urlService.getFavicon(shortLinkCreateParam.getOriginUrl());
        //构建实体
        ShortLinkDO shortLinkDO = ShortLinkConverter.buildShortLink(shortUri, fullShortUrl, shortLinkCreateParam, shortLinkCreateParam.getUsername(), favicon);
        //构建路由表实体
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder().username(shortLinkCreateParam.getUsername()).gid(
                shortLinkCreateParam.getGid()).fullShortUrl(shortLinkDO.getFullShortUrl()).delFlag(YesOrNoEnum.YES.getCode()).build();
        try {
            shortLinkDao.save(shortLinkDO);
            shortLinkGoToDao.save(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
            log.warn("短链接：{} 重复入库", fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        //加入布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        //放入缓存,缓存预热
        long seconds = 0;
        if (shortLinkDO.getValidDateType() == ValidTypeEnum.FOREVER.getType()) {
            seconds = ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;
        } else {
            seconds = LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate());
        }
        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkCreateParam.getOriginUrl(), seconds, TimeUnit.SECONDS);
        return ShortLinkConverter.buildShortLinkCreateResp(shortLinkDO, officialDomain);
    }

    @Override
    @Transactional
    public void updateShortLink(ShortLinkUpdateParam requestParam) {
        //查询更改的短链接
        ShortLinkDO oldLink = shortLinkDao.getOneByConditions(requestParam.getUsername(), requestParam.getFullShortUrl());
        AssertUtil.notNull(oldLink, "短链接不存在");
        if (Objects.equals(oldLink.getGid(), requestParam.getGid())) {
            ShortLinkDO shortLinkDO = new ShortLinkDO();
            BeanUtils.copyProperties(oldLink, shortLinkDO);
            shortLinkDO.setOriginUrl(requestParam.getOriginUrl());
            shortLinkDO.setDescribe(requestParam.getDescribe());
            shortLinkDO.setValidDateType(requestParam.getValidDateType());
            shortLinkDO.setValidDate(requestParam.getValidDate());
            shortLinkDao.save(shortLinkDO);
            shortLinkDao.delete(oldLink);
        } else {
            // 为什么监控表要加上Gid？不加的话是否就不存在读写锁？详情查看：https://nageoffer.com/shortlink/question
            RReadWriteLock
                    readWriteLock = redissonClient.getReadWriteLock(String.format(RedisConstant.LOCK_GID_UPDATE_KEY,
                    requestParam.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();
            try {
//                ShortLinkDO delShortLinkDO = ShortLinkDO.builder()
//                        .build();
//                delShortLinkDO.setDelFlag(1);
//                delShortLinkDO.setFullShortUrl(oldLink.getFullShortUrl());
//                delShortLinkDO.setUsername(oldLink.getUsername());
//                shortLinkDao.updateByConditions(delShortLinkDO);
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .domain(officialDomain)
                        .username(oldLink.getUsername())
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(oldLink.getCreatedType())
                        .validDateType(requestParam.getValidDateType())
                        .validDate(requestParam.getValidDate())
                        .describe(requestParam.getDescribe())
                        .shortUri(oldLink.getShortUri())
                        .enableStatus(oldLink.getEnableStatus())
                        .totalPv(oldLink.getTotalPv())
                        .totalUv(oldLink.getTotalUv())
                        .totalUip(oldLink.getTotalUip())
                        .fullShortUrl(oldLink.getFullShortUrl())
                        .favicon(urlService.getFavicon(requestParam.getOriginUrl()))
                        .build();
                shortLinkDao.updateByConditions(shortLinkDO);
                ShortLinkGotoDO shortLinkGotoDO =
                        shortLinkGoToDao.getByFullShortUrlAndUserName(requestParam.getFullShortUrl(),
                                oldLink.getUsername());
                shortLinkGoToDao.deleteByConditions(shortLinkGotoDO);
                shortLinkGotoDO.setGid(requestParam.getGid());
                shortLinkGoToDao.save(shortLinkGotoDO);
            } finally {
                rLock.unlock();
            }
        }
        // 短链接如何保障缓存和数据库一致性？详情查看：https://nageoffer.com/shortlink/question
        if (!Objects.equals(oldLink.getValidDateType(), requestParam.getValidDateType())
                || !Objects.equals(oldLink.getValidDate(), requestParam.getValidDate())
                || !Objects.equals(oldLink.getOriginUrl(), requestParam.getOriginUrl())) {
            stringRedisTemplate.delete(LINK_GOTO_PRE_KEY + requestParam.getFullShortUrl());
            Date currentDate = new Date();
            if (oldLink.getValidDate() != null && oldLink.getValidDate().before(currentDate)) {
                if (Objects.equals(requestParam.getValidDateType(), ValidTypeEnum.FOREVER.getType()) || requestParam.getValidDate().after(currentDate)) {
                    stringRedisTemplate.delete(LINK_GOTO_IS_NULL_PRE_KEY + requestParam.getFullShortUrl());
                }
            }
        }
    }

    @Override
    public PageDTO<ShortLinkRespDTO> pageLink(ShortLinkPageParam shortLinkPageParam) {
        IPage<ShortLinkDO> linkPage = shortLinkDao.pageLinkList(shortLinkPageParam);
        List<ShortLinkDO> links = Optional.ofNullable(linkPage).map(IPage::getRecords).get();
        if (CollUtil.isNotEmpty(links)) {
            List<ShortLinkRespDTO> shortLinkRespDTOS = links.stream()
                    .map(shortLink -> {
                        ShortLinkRespDTO shortLinkRespDTO = BeanConverter.CONVERTER.converterLink1(shortLink);
                        //判断domain是否是自定义的
                        if (shortLink.getDomain().equals(officialDomain)) {
                            //TODO 真正上线是https
                            shortLinkRespDTO.setFullShortUrl(shortLink.getFullShortUrl());
                        } else {
                            shortLinkRespDTO.setFullShortUrl(shortLink.getFullShortUrl());
                        }
                        return shortLinkRespDTO;
                    }).toList();
            PageDTO<ShortLinkRespDTO> pageDTO = new PageDTO<>();
            pageDTO.setRecords(shortLinkRespDTOS);
            pageDTO.setPages(linkPage.getPages());
            pageDTO.setSize(linkPage.getSize());
            pageDTO.setTotal(linkPage.getTotal());
            pageDTO.setCurrent(linkPage.getCurrent());
            return pageDTO;
        }
        return null;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupLinkCount(List<String> requestParam, String username) {
        //查询gid对应数量
        return shortLinkDao.getGroupLinkCount(requestParam, username);
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateParam requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateParam shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateParam.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLink = createLink(shortLinkCreateReqDTO);
                ShortLinkBaseInfoRespDTO linkBaseInfoRespDTO = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .describe(describes.get(i))
                        .build();
                result.add(linkBaseInfoRespDTO);
            } catch (Throwable ex) {
                log.error("批量创建短链接失败，原始参数：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .baseLinkInfos(result)
                .build();
    }

    private String generateSuffix(ShortLinkCreateParam dto) {
        int customGenerateCount = 0;
        String shorUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            //加上时间戳,防止同样的短链接在不同时间无法申请
            String originUrl = dto.getOriginUrl();
            originUrl += System.currentTimeMillis();
            shorUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(dto.getDomain() + "/" + shorUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }


    private void setHotKeyOrNull(String key, Object value) {
        boolean isHotKey = JdHotKeyStore.isHotKey(key);
        if (isHotKey) {
            JdHotKeyStore.smartSet(key, value);
        }
    }


}
