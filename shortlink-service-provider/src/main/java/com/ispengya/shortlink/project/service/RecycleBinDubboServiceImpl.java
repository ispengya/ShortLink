package com.ispengya.shortlink.project.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ispengya.shortlink.common.constant.RedisConstant;
import static com.ispengya.shortlink.common.constant.RedisConstant.LINK_GOTO_PRE_KEY;
import com.ispengya.shortlink.common.constant.ShortLinkConstant;
import com.ispengya.shortlink.common.converter.BeanConverter;
import com.ispengya.shortlink.common.enums.ValidTypeEnum;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.dao.ShortLinkGoToDao;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.dto.request.RecycleBinPageParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRecoverParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRemoveParam;
import com.ispengya.shortlink.project.dto.request.RecycleSaveParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
import com.ispengya.shortlink.project.util.LinkUtil;
import com.ispengya.travel.frameworks.starter.cache.toolkit.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author ispengya
 * @date 2023/12/9 16:27
 */
@Service
@DubboService
@RequiredArgsConstructor
public class RecycleBinDubboServiceImpl implements RecycleBinDubboService {

    @Value("${short-link.official.domain}")
    private String officialDomain;
    private final ShortLinkDao shortLinkDao;
    private final ShortLinkGoToDao shortLinkGoToDao;

    @Override
    public void save(RecycleSaveParam reqDTO) {
        //查询到短链接
        ShortLinkDO shortLinkDO = shortLinkDao.getOneByConditions(reqDTO.getUsername(), reqDTO.getFullShortUrl());
        shortLinkDO.setEnableStatus(YesOrNoEnum.NO.getCode());
        shortLinkDO.setUpdateTime(null);
        shortLinkDao.saveRecycle(shortLinkDO);
        shortLinkGoToDao.updateStatus(reqDTO.getUsername(),reqDTO.getFullShortUrl(),YesOrNoEnum.NO);
        //删除redis缓存
        RedisUtils.del(RedisConstant.LINK_GOTO_PRE_KEY+reqDTO.getFullShortUrl());
    }

    @Override
    public PageDTO<ShortLinkRespDTO> pageList(RecycleBinPageParam reqDTO) {
        IPage<ShortLinkDO> linkPage = shortLinkDao.pageRecycleOfLink(reqDTO);
        List<ShortLinkDO> links = Optional.ofNullable(linkPage).map(IPage::getRecords).get();
        if (CollUtil.isNotEmpty(links)) {
            List<ShortLinkRespDTO> shortLinkRespDTOS = links.stream()
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
                    }).toList();
            PageDTO<ShortLinkRespDTO> pageDTO =new PageDTO<>();
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
    public void recover(RecycleBinRecoverParam reqDTO) {
        ShortLinkDO shortLinkDO = shortLinkDao.getOneInRecycle(reqDTO.getUsername(), reqDTO.getFullShortUrl());
        shortLinkDO.setEnableStatus(YesOrNoEnum.YES.getCode());
        //否则自动填充更新字段不开启
        shortLinkDO.setUpdateTime(null);
        shortLinkDao.recoverInRecycle(shortLinkDO);
        shortLinkGoToDao.updateStatus(reqDTO.getUsername(),reqDTO.getFullShortUrl(),YesOrNoEnum.YES);
        //缓存预热和删除缓存空值
        RedisUtils.del(RedisConstant.LINK_GOTO_IS_NULL_PRE_KEY + reqDTO.getFullShortUrl());
        //判断有效期是否永久
        if (!Objects.equals(shortLinkDO.getValidDateType(), ValidTypeEnum.FOREVER.getType())) {
            RedisUtils.set(LINK_GOTO_PRE_KEY + reqDTO.getFullShortUrl(), shortLinkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
            );
        } else {
            //在缓存过期默认一个月
            RedisUtils.set(LINK_GOTO_PRE_KEY + reqDTO.getFullShortUrl(), shortLinkDO.getOriginUrl(), ShortLinkConstant.DEFAULT_CACHE_VALID_TIME, TimeUnit.SECONDS);
        }
    }

    @Override
    public void remove(RecycleBinRemoveParam reqDTO) {
        shortLinkDao.removeByConditions(reqDTO);
        //TODO 删除路由表
        //删除缓存
        RedisUtils.del(RedisConstant.LINK_GOTO_PRE_KEY+reqDTO.getFullShortUrl());
    }
}
