package com.ispengya.shortlink.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.common.util.HashUtil;
import com.ispengya.shortlink.project.dao.ShortLinkDao;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkUpdateReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import com.ispengya.shortlink.project.service.ShortLinkService;
import com.ispengya.shortlink.project.service.converter.BeanConverter;
import com.ispengya.shortlink.project.service.converter.ShortLinkConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

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
        try {
            //判断domain是否是自定义的
            if (shortLinkCreateReqDTO.getDomain().equals(officialDomain)){
                shortLink.setFullShortUrl("https://"+shortLink.getFullShortUrl());
            }else {
                shortLink.setFullShortUrl("http://"+shortLink.getFullShortUrl());
            }
            shortLinkDao.save(shortLink);
        } catch (DuplicateKeyException ex) {
            log.warn("短链接：{} 重复入库", fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        //加入布隆过滤器
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLink.getFullShortUrl())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .gid(shortLinkCreateReqDTO.getGid())
                .build();
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        //查询更改的短链接
        ShortLink oldLink = shortLinkDao.getOneByConditions(shortLinkUpdateReqDTO.getUsername(),shortLinkUpdateReqDTO.getFullShortUrl());
        AssertUtil.notNull(oldLink,"短链接不存在");
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
            if (!shortUriCreateCachePenetrationBloomFilter.contains(dto.getDomain()+"/"+shorUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }


}
