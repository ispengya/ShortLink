package com.ispengya.shortlink.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.project.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.project.common.util.HashUtil;
import com.ispengya.shortlink.project.common.util.LinkUtil;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.dao.ShortLinkGoToDao;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkUpdateReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import com.ispengya.shortlink.project.domain.eneity.ShortLinkGoto;
import com.ispengya.shortlink.project.service.ShortLinkService;
import com.ispengya.shortlink.project.service.converter.BeanConverter;
import com.ispengya.shortlink.project.service.converter.ShortLinkConverter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ispengya.shortlink.common.constant.RedisConstant.*;

/**
 * @author ispengya
 * @date 2023/11/25 14:20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Value("${official.domain}")
    private String officialDomain;
    private final ShortLinkDao shortLinkDao;
    private final ShortLinkGoToDao shortLinkGoToDao;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    @SneakyThrows
    public void jumpUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        //先从缓存获取
        String originLink = stringRedisTemplate.opsForValue().get(LINK_GOTO_PRE_KEY + fullShortUrl);
        if (StrUtil.isNotBlank(originLink)) {
            ((HttpServletResponse) response).sendRedirect(originLink);
            return;
        }
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
                ((HttpServletResponse) response).sendRedirect(originLink);
                return;
            }
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
                ((HttpServletResponse) response).sendRedirect(shortLink.getOriginUrl());
            } else {
                stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLink.getOriginUrl());
                ((HttpServletResponse) response).sendRedirect(shortLink.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ShortLinkCreateRespDTO createLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        //生成短链接uri
        String shortUri = generateSuffix(shortLinkCreateReqDTO);
        String fullShortUrl = StrBuilder.create(shortLinkCreateReqDTO.getDomain())
                .append("/")
                .append(shortUri)
                .toString();
        //TODO
        String favicon = "test";
        //构建实体
        ShortLink shortLink = ShortLinkConverter.buildShortLink(shortUri, fullShortUrl, shortLinkCreateReqDTO, shortLinkCreateReqDTO.getUsername(), favicon);
        //构建路由表实体
        ShortLinkGoto shortLinkGoto = ShortLinkGoto.builder().username(shortLinkCreateReqDTO.getUsername()).gid(shortLinkCreateReqDTO.getGid()).fullShortUrl(shortLink.getFullShortUrl()).build();
        try {
            //判断domain是否是自定义的
            if (shortLinkCreateReqDTO.getDomain().equals(officialDomain)) {
                //TODO 真正上线是https
                shortLink.setFullShortUrl("http://" + shortLink.getFullShortUrl());
            } else {
                shortLink.setFullShortUrl("http://" + shortLink.getFullShortUrl());
            }
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
        stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + fullShortUrl, shortLinkCreateReqDTO.getOriginUrl(), seconds, TimeUnit.SECONDS);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLink.getFullShortUrl())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .gid(shortLinkCreateReqDTO.getGid())
                .build();
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        //查询更改的短链接
        ShortLink oldLink = shortLinkDao.getOneByConditions(shortLinkUpdateReqDTO.getUsername(), shortLinkUpdateReqDTO.getFullShortUrl());
        AssertUtil.notNull(oldLink, "短链接不存在");
        oldLink.setGid(shortLinkUpdateReqDTO.getGid());
        oldLink.setDescribe(shortLinkUpdateReqDTO.getDescribe());
        oldLink.setOriginUrl(shortLinkUpdateReqDTO.getOriginUrl());
        oldLink.setValidDateType(shortLinkUpdateReqDTO.getValidDateType());
        oldLink.setValidDate(shortLinkUpdateReqDTO.getValidDate());
        shortLinkDao.updateByConditions(oldLink);
    }

    @Override
    public List<ShortLinkRespDTO> pageLink(ShortLinkPageReq shortLinkPageReq) {
        IPage<ShortLink> linkPage = shortLinkDao.pageLink(shortLinkPageReq);
        List<ShortLink> links = Optional.ofNullable(linkPage).map(IPage::getRecords).get();
        if (CollUtil.isNotEmpty(links)) {
            return links.stream()
                    .map(BeanConverter.CONVERTER::converterLink1)
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupLinkCount(List<String> requestParam, String username) {
        //查询gid对应数量
        return shortLinkDao.getGroupLinkCount(requestParam, username);
    }

    public String generateSuffix(ShortLinkCreateReqDTO dto) {
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


}
