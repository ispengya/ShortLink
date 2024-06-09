package com.ispengya.shortlink.project.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_IS_NULL_PRE_KEY;
import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_PRE_KEY;
import static com.ispengya.shortlink.common.constant.RedisConstant.LOCK_GET_ORIGIN_LINK_PRE_KEY;
import com.ispengya.shortlink.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.common.converter.BeanConverter;
import com.ispengya.shortlink.common.converter.ShortLinkConverter;
import com.ispengya.shortlink.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.dao.ShortLinkGoToDao;
import com.ispengya.shortlink.project.domain.LinkStatsMQDTO;
import com.ispengya.shortlink.project.domain.ShortLink;
import com.ispengya.shortlink.project.domain.ShortLinkGoto;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
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
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Value("${official.domain}")
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
                    ShortLinkGoto shortLinkGoto = shortLinkGoToDao.getByFullShortUrl(fullShortUrl);
                    if (shortLinkGoto == null) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null");
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }
                    ShortLink shortLink = shortLinkDao.getOneByConditions(shortLinkGoto.getUsername(), shortLinkGoto.getFullShortUrl());
                    if (shortLink == null || (shortLink.getValidDate()!=null && shortLink.getValidDateType().equals(ValidTypeEnum.CUSTOM.getType()) && shortLink.getValidDate().before(new Date()))) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null", 30, TimeUnit.MINUTES);
                        ((HttpServletResponse) response).sendRedirect("/page/notfound");
                        return;
                    }
                    //判断有效期是否永久
                    if (!Objects.equals(shortLink.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLink.getOriginUrl(),
                                LinkUtil.getLinkCacheValidTime(shortLink.getValidDate()), TimeUnit.MILLISECONDS
                        );
                    } else {
                        //在缓存过期默认一个月
                        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLink.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
                        ((HttpServletResponse) response).sendRedirect(shortLink.getOriginUrl());
                    }
                    originLink = shortLink.getOriginUrl();
                }
            } finally {
                lock.unlock();
            }
        }
        //进行重定向
        if (StrUtil.isNotBlank(originLink)) {
            //异步统计
            linkStatsProducer.sendMsg(fullShortUrl,request,response);
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
                        ShortLinkGoto shortLinkGoto = shortLinkGoToDao.getByFullShortUrl(fullShortUrl);
                        if (shortLinkGoto == null) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null");
                            ((HttpServletResponse) response).sendRedirect("/page/notfound");
                            return;
                        }
                        ShortLink shortLink = shortLinkDao.getOneByConditions(shortLinkGoto.getUsername(), shortLinkGoto.getFullShortUrl());
                        if (shortLink == null || (shortLink.getValidDateType().equals(ValidTypeEnum.CUSTOM.getType()) && shortLink.getValidDate().before(new Date()))) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_IS_NULL_PRE_KEY + fullShortUrl, "null", 30, TimeUnit.MINUTES);
                            ((HttpServletResponse) response).sendRedirect("/page/notfound");
                            return;
                        }
                        //判断有效期是否永久
                        if (!Objects.equals(shortLink.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLink.getOriginUrl(),
                                    LinkUtil.getLinkCacheValidTime(shortLink.getValidDate()), TimeUnit.MILLISECONDS
                            );
                            originLink = shortLink.getOriginUrl();
                        } else {
                            originLink = shortLink.getOriginUrl();
                            //在缓存过期默认一个月
                            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLink.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
                            ((HttpServletResponse) response).sendRedirect(shortLink.getOriginUrl());
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
        ShortLink shortLink = ShortLinkConverter.buildShortLink(shortUri, fullShortUrl, shortLinkCreateParam, shortLinkCreateParam.getUsername(), favicon);
        //构建路由表实体
        ShortLinkGoto shortLinkGoto = ShortLinkGoto.builder().username(shortLinkCreateParam.getUsername()).gid(
                shortLinkCreateParam.getGid()).fullShortUrl(shortLink.getFullShortUrl()).build();
        try {
            shortLinkDao.save(shortLink);
            shortLinkGoToDao.save(shortLinkGoto);
        } catch (DuplicateKeyException ex) {
            log.warn("短链接：{} 重复入库", fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        //加入布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        //放入缓存,缓存预热
        long seconds = 0;
        if (shortLink.getValidDateType() == ValidTypeEnum.FOREVER.getType()) {
            seconds = ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;
        } else {
            seconds = LinkUtil.getLinkCacheValidTime(shortLink.getValidDate());
        }
        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkCreateParam.getOriginUrl(), seconds, TimeUnit.SECONDS);
        return ShortLinkConverter.buildShortLinkCreateResp(shortLink, officialDomain);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateParam shortLinkUpdateParam) {
        //查询更改的短链接
        ShortLink oldLink = shortLinkDao.getOneByConditions(shortLinkUpdateParam.getUsername(), shortLinkUpdateParam.getFullShortUrl());
        AssertUtil.notNull(oldLink, "短链接不存在");
        oldLink.setGid(shortLinkUpdateParam.getGid());
        oldLink.setDescribe(shortLinkUpdateParam.getDescribe());
        oldLink.setOriginUrl(shortLinkUpdateParam.getOriginUrl());
        oldLink.setValidDateType(shortLinkUpdateParam.getValidDateType());
        oldLink.setValidDate(shortLinkUpdateParam.getValidDate());
        //这样才会自动更新时间
        oldLink.setUpdateTime(null);
        shortLinkDao.updateByConditions(oldLink);
    }

    @Override
    public List<ShortLinkRespDTO> pageLink(ShortLinkPageParam shortLinkPageParam) {
        IPage<ShortLink> linkPage = shortLinkDao.pageLinkList(shortLinkPageParam);
        List<ShortLink> links = Optional.ofNullable(linkPage).map(IPage::getRecords).get();
        if (CollUtil.isNotEmpty(links)) {
            return links.stream()
                    .map(shortLink -> {
                        ShortLinkRespDTO shortLinkRespDTO = BeanConverter.CONVERTER.converterLink1(shortLink);
                        //判断domain是否是自定义的
                        if (shortLink.getDomain().equals(officialDomain)) {
                            //TODO 真正上线是https
                            shortLinkRespDTO.setFullShortUrl("http://" + shortLink.getFullShortUrl());
                        } else {
                            shortLinkRespDTO.setFullShortUrl("http://" + shortLink.getFullShortUrl());
                        }
                        return shortLinkRespDTO;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupLinkCount(List<String> requestParam, String username) {
        //查询gid对应数量
        return shortLinkDao.getGroupLinkCount(requestParam, username);
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

    private void sendMq(LinkStatsMQDTO body) {

    }

    private void setHotKeyOrNull(String key, Object value) {
        boolean isHotKey = JdHotKeyStore.isHotKey(key);
        if (isHotKey) {
            JdHotKeyStore.smartSet(key, value);
        }
    }


}
