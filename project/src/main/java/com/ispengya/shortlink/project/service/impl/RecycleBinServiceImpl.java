package com.ispengya.shortlink.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ispengya.shortlink.common.constant.RedisConstant;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.project.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.project.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.project.common.util.LinkUtil;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinPageReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRecoverReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRemoveReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleSaveReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import com.ispengya.shortlink.project.service.RecycleBinService;
import com.ispengya.shortlink.project.service.converter.BeanConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_PRE_KEY;

/**
 * @author ispengya
 * @date 2023/12/9 16:27
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    @Value("${official.domain}")
    private String officialDomain;
    private final ShortLinkDao shortLinkDao;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(RecycleSaveReqDTO reqDTO) {
        //查询到短链接
        ShortLink shortLink = shortLinkDao.getOneByConditions(reqDTO.getUsername(), reqDTO.getFullShortUrl());
        shortLink.setEnableStatus(YesOrNoEnum.NO.getCode());
        shortLinkDao.updateByConditions(shortLink);
        //删除redis缓存
        stringRedisTemplate.delete(RedisConstant.LINK_GOTO_PRE_KEY+reqDTO.getFullShortUrl());
    }

    @Override
    public List<ShortLinkRespDTO> pageList(RecycleBinPageReqDTO reqDTO) {
        IPage<ShortLink> linkPage = shortLinkDao.pageRecycleOfLink(reqDTO);
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
    public void recover(RecycleBinRecoverReqDTO reqDTO) {
        ShortLink shortLink = shortLinkDao.getOneInRecycle(reqDTO.getUsername(), reqDTO.getFullShortUrl());
        shortLink.setEnableStatus(YesOrNoEnum.YES.getCode());
        shortLinkDao.recoverInRecycle(shortLink);
        //缓存预热和删除缓存空值
        stringRedisTemplate.delete(RedisConstant.LINK_GOTO_IS_NULL_PRE_KEY + reqDTO.getFullShortUrl());
        //判断有效期是否永久
        if (!Objects.equals(shortLink.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + reqDTO.getFullShortUrl(), shortLink.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(shortLink.getValidDate()), TimeUnit.MILLISECONDS
            );
        } else {
            //在缓存过期默认一个月
            stringRedisTemplate.opsForValue().set(LINK_GOTO_PRE_KEY + reqDTO.getFullShortUrl(), shortLink.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
        }
    }

    @Override
    public void remove(RecycleBinRemoveReqDTO reqDTO) {
        shortLinkDao.removeByConditions(reqDTO);
        //删除缓存
        stringRedisTemplate.delete(RedisConstant.LINK_GOTO_PRE_KEY+reqDTO.getFullShortUrl());
    }
}
