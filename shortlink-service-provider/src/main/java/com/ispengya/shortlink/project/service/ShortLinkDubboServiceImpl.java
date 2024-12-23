package com.ispengya.shortlink.project.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ispengya.shortlink.common.algorithm.link.GenerateService;
import com.ispengya.shortlink.common.constant.RedisConstant;
import com.ispengya.shortlink.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.common.converter.BeanConverter;
import com.ispengya.shortlink.common.converter.ShortLinkConverter;
import com.ispengya.shortlink.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.mq.producer.LinkStatsProducer;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.dao.ShortLinkGoToDao;
import com.ispengya.shortlink.project.dao.ShortLinkStatsDao;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.domain.ShortLinkGotoDO;
import com.ispengya.shortlink.project.domain.dao.LinkStatsTodayDTO;
import com.ispengya.shortlink.project.dto.request.ShortLinkBatchCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.*;
import com.ispengya.shortlink.project.util.HashUtil;
import com.ispengya.shortlink.project.util.LinkUtil;
import lombok.RequiredArgsConstructor;
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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ispengya.shortlink.common.constant.RedisConstant.*;

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
    private final GenerateService generateService;
    private final ShortLinkStatsDao shortLinkStatsDao;
    private final ShortLinkGoToDao shortLinkGoToDao;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final UrlDubboServiceImpl urlService;
    private final RocketMQTemplate rocketMQTemplate;
    private final LinkStatsProducer linkStatsProducer;

    @Override
    public void jumpUrlV1(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;

        //获取原始链接
        String originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
        if (StrUtil.isEmpty(originLink)) {
            //分布式锁避免缓存击穿
            RLock lock = redissonClient.getLock(LOCK_GET_ORIGIN_LINK_PRE_KEY + fullShortUrl);
            lock.lock();
            try {
                //双重检测
                originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
                if (StrUtil.isBlank(originLink)) {
                    //缓存null值，避免缓存穿透
                    String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_IS_NULL_PRE_KEY +
                            fullShortUrl);
                    if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }

                    //load db
                    ShortLinkDO linkDO = shortLinkDao.getOneByConditions(null, fullShortUrl);
                    if (linkDO == null || LinkUtil.isExpireLink(linkDO)) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null");
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }

                    //判断有效期是否永久，设置缓存
                    if (!Objects.equals(linkDO.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
                        long linkCacheValidTime = LinkUtil.getLinkCacheValidTime(linkDO.getValidDate());
                        //目的过期之前，缓存提前清除（定时扫描表，在即将过期之前删除缓存，需要结合redis过期键清除配置策略）
                        if (linkCacheValidTime > 12 * 60 * 60) {
                            linkCacheValidTime -= 6 * 60 * 60;
                        }
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, linkDO.getOriginUrl(),
                                linkCacheValidTime, TimeUnit.MILLISECONDS
                        );
                    } else {
                        //在缓存过期默认一个月
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, linkDO.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
                    }
                    originLink = linkDO.getOriginUrl();
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
    public ShortLinkCreateRespDTO createLink(ShortLinkCreateParam shortLinkCreateParam) {
        //生成短链接uri
        String shortUri = null;
        try {
            shortUri = generateSuffix(shortLinkCreateParam);
        } catch (InterruptedException e) {
            log.error("generate uri error:{}", e.getMessage());
        }
        String fullShortUrl = StrBuilder.create(shortLinkCreateParam.getDomain())
                .append("/")
                .append(shortUri)
                .toString();
        //获取favicon
        String favicon = "https://link.zaizaige.top/favicon.ico";
        //构建实体
        ShortLinkDO shortLinkDO = ShortLinkConverter.buildShortLink(shortUri, fullShortUrl, shortLinkCreateParam, shortLinkCreateParam.getUsername(), favicon);
        //构建路由表实体
        try {
            shortLinkDao.save(shortLinkDO);
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
    @Deprecated
    public ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkCreateParam requestParam) {
//        String fullShortUrl;
//        // 为什么说布隆过滤器性能远胜于分布式锁？详情查看：https://nageoffer.com/shortlink/question
//        RLock lock = redissonClient.getLock(SHORT_LINK_CREATE_LOCK_KEY);
//        lock.lock();
//        try {
//            String shortLinkSuffix = generateSuffixByLock(requestParam);
//            fullShortUrl = StrBuilder.create(requestParam.getDomain())
//                    .append("/")
//                    .append(shortLinkSuffix)
//                    .toString();
//            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
//                    .domain(requestParam.getDomain())
//                    .originUrl(requestParam.getOriginUrl())
//                    .gid(requestParam.getGid())
//                    .username(requestParam.getUsername())
//                    .createdType(requestParam.getCreatedType())
//                    .validDateType(requestParam.getValidDateType())
//                    .validDate(requestParam.getValidDate())
//                    .describe(requestParam.getDescribe())
//                    .shortUri(shortLinkSuffix)
//                    .enableStatus(0)
//                    .totalPv(0)
//                    .totalUv(0)
//                    .totalUip(0)
//                    .fullShortUrl(fullShortUrl)
//                    .favicon(urlService.getFavicon(requestParam.getOriginUrl()))
//                    .build();
//            ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
//                    .fullShortUrl(fullShortUrl)
//                    .gid(requestParam.getGid())
//                    .username(requestParam.getUsername())
//                    .build();
//            try {
//                shortLinkDao.save(shortLinkDO);
//                shortLinkGoToDao.save(linkGotoDO);
//            } catch (DuplicateKeyException ex) {
//                throw new ServiceException(String.format("短链接：%s 生成重复", fullShortUrl));
//            }
//            stringRedisTemplate.opsForValue().set(
//                    LINK_GOTO_PRE_KEY + fullShortUrl,
//                    requestParam.getOriginUrl(),
//                    LinkUtil.getLinkCacheValidTime(requestParam.getValidDate()), TimeUnit.MILLISECONDS
//            );
//        } finally {
//            lock.unlock();
//        }
//        return ShortLinkCreateRespDTO.builder()
//                .fullShortUrl("http://" + fullShortUrl)
//                .originUrl(requestParam.getOriginUrl())
//                .gid(requestParam.getGid())
//                .build();
        return null;
    }

    @Override
    @Transactional
    public void updateShortLinkV2(ShortLinkUpdateParam requestParam){
        //查询短链接
        ShortLinkDO oldLink = shortLinkDao.getOneByConditions(requestParam.getUsername(), requestParam.getFullShortUrl());
        AssertUtil.notNull(oldLink, "短链接不存在");

        //获取旧值
        Integer validDateType = oldLink.getValidDateType();
        Date validDate = oldLink.getValidDate();
        String originUrl = oldLink.getOriginUrl();

        //更新新值
        oldLink.setOriginUrl(requestParam.getOriginUrl());
        oldLink.setGid(requestParam.getGid());
        oldLink.setValidDateType(requestParam.getValidDateType());
        oldLink.setValidDate(requestParam.getValidDate());
        oldLink.setDescribe(requestParam.getDescribe());
        shortLinkDao.updateByConditions(oldLink);

        //三个字段更新，缓存更新
        if (!Objects.equals(validDateType, requestParam.getValidDateType())
                || !Objects.equals(validDate, requestParam.getValidDate())
                || !Objects.equals(originUrl, requestParam.getOriginUrl())) {
            stringRedisTemplate.delete(LINK_GOTO_PRE_KEY + requestParam.getFullShortUrl());
            Date currentDate = new Date();
            if (validDate != null && validDate.before(currentDate)) {
                //链接原本是一个自定义时间的，过期之后进行更新，如果在这期间进行查找，就会出现缓存空值情况
                if (Objects.equals(requestParam.getValidDateType(), ValidTypeEnum.FOREVER.getType()) || requestParam.getValidDate().after(currentDate)) {
                    stringRedisTemplate.delete(LINK_GOTO_IS_NULL_PRE_KEY + requestParam.getFullShortUrl());
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateShortLink(ShortLinkUpdateParam requestParam) {
        //查询更改的短链接
        ShortLinkDO oldLink = shortLinkDao.getOneByConditions(requestParam.getUsername(), requestParam.getFullShortUrl());
        AssertUtil.notNull(oldLink, "短链接不存在");

        //分组是否变更
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
            // 为什么分组变更要加锁，
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
        //查询link集合
        IPage<ShortLinkDO> linkPage = shortLinkDao.pageLinkList(shortLinkPageParam);
        if (Objects.nonNull(linkPage) && CollectionUtil.isNotEmpty(linkPage.getRecords())) {
            //查今日统计数据
            List<LinkStatsTodayDTO> statsTodayDTOList = shortLinkStatsDao.selectListByShortUrls(linkPage.getRecords().stream().map(ShortLinkDO::getFullShortUrl).collect(Collectors.toList()));
            Map<String, LinkStatsTodayDTO> linkStatsTodayMap = statsTodayDTOList.stream().collect(Collectors.toMap(LinkStatsTodayDTO::getShortUrl, Function.identity()));
            List<ShortLinkDO> links = linkPage.getRecords();
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

                        //设置今日统计数据
                        LinkStatsTodayDTO linkStatsTodayDTO = linkStatsTodayMap.get(shortLink.getFullShortUrl());
                        shortLinkRespDTO.setTodayPv(linkStatsTodayDTO.getTodayPv());
                        shortLinkRespDTO.setTodayUv(linkStatsTodayDTO.getTodayUv());
                        shortLinkRespDTO.setTodayUip(linkStatsTodayDTO.getTodayUip());

                        return shortLinkRespDTO;
                    }).collect(Collectors.toList());
            //返回分页数据
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

    private String generateSuffix(ShortLinkCreateParam dto) throws InterruptedException {
        int customGenerateCount = 0;
        String shorUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            //加上时间戳,防止同样的短链接在不同时间无法申请
//            String originUrl = dto.getOriginUrl();
//            originUrl += UUID.randomUUID().toString();
//            shorUri = HashUtil.hashToBase62(originUrl);
            shorUri = generateService.generateShortUrl(dto.getOriginUrl());
            if (!shortUriCreateCachePenetrationBloomFilter.contains(dto.getDomain() + "/" + shorUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }

    private String generateSuffixByLock(ShortLinkCreateParam requestParam) {
        int customGenerateCount = 0;
        String shorUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            // 短链接哈希算法生成冲突问题如何解决？详情查看：https://nageoffer.com/shortlink/question
            shorUri = HashUtil.hashToBase62(originUrl);
            ShortLinkDO shortLinkDO = shortLinkDao.getOneByConditions(requestParam.getUsername(), shorUri);
            if (shortLinkDO == null) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }
}
