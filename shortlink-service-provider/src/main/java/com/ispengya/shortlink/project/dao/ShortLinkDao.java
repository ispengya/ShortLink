package com.ispengya.shortlink.project.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.project.domain.ShortLink;
import com.ispengya.shortlink.project.dto.request.RecycleBinPageParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRemoveParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.mapper.ShortLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:17
 */
@Repository
@RequiredArgsConstructor
public class ShortLinkDao extends ServiceImpl<ShortLinkMapper, ShortLink> {

    private final ShortLinkMapper shortLinkMapper;


    public IPage<ShortLink> pageLinkList(ShortLinkPageParam shortLinkPageParam) {
        IPage<ShortLink> page = new Page<>(shortLinkPageParam.getCurrent(), shortLinkPageParam.getPageSize());
        LambdaQueryWrapper<ShortLink> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLink::getGid, shortLinkPageParam.getGid());
        queryWrapper.eq(ShortLink::getUsername, shortLinkPageParam.getUsername());
        queryWrapper.eq(ShortLink::getEnableStatus, YesOrNoEnum.YES.getCode());
        queryWrapper.eq(ShortLink::getDelFlag, YesOrNoEnum.YES.getCode());
        return baseMapper.selectPage(page, queryWrapper);
    }

    public List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(List<String> requestParam, String username) {
        return shortLinkMapper.getGroupLinkCount(requestParam, username);
    }

    public ShortLink getOneByConditions(String username, String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLink::getUsername,username)
                .eq(ShortLink::getShortUri,getShortUri(fullShortUrl))
                .eq(ShortLink::getEnableStatus,YesOrNoEnum.YES.getCode())
                .eq(ShortLink::getDelFlag,YesOrNoEnum.YES.getCode())
                .one();
    }

    public ShortLink getOneInRecycle(String username, String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLink::getUsername,username)
                .eq(ShortLink::getShortUri,getShortUri(fullShortUrl))
                .eq(ShortLink::getEnableStatus,YesOrNoEnum.NO.getCode())
                .eq(ShortLink::getDelFlag,YesOrNoEnum.YES.getCode())
                .one();
    }

    public void updateByConditions(ShortLink oldLink) {
        lambdaUpdate()
                .eq(ShortLink::getUsername,oldLink.getUsername())
                .eq(ShortLink::getShortUri,getShortUri(oldLink.getFullShortUrl()))
                .eq(ShortLink::getEnableStatus,YesOrNoEnum.YES.getCode())
                .eq(ShortLink::getDelFlag,YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public void saveRecycle(ShortLink oldLink) {
        lambdaUpdate()
                .eq(ShortLink::getUsername,oldLink.getUsername())
                .eq(ShortLink::getShortUri,getShortUri(oldLink.getFullShortUrl()))
                .eq(ShortLink::getEnableStatus,YesOrNoEnum.YES.getCode())
                .eq(ShortLink::getDelFlag,YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public void recoverInRecycle(ShortLink oldLink) {
        lambdaUpdate()
                .eq(ShortLink::getUsername,oldLink.getUsername())
                .eq(ShortLink::getShortUri,getShortUri(oldLink.getFullShortUrl()))
                .eq(ShortLink::getDelFlag,YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public IPage<ShortLink> pageRecycleOfLink(RecycleBinPageParam reqDTO) {
        IPage<ShortLink> page = new Page<>(reqDTO.getCurrent(), reqDTO.getPageSize());
        LambdaQueryWrapper<ShortLink> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLink::getUsername, reqDTO.getUsername());
        queryWrapper.eq(ShortLink::getEnableStatus, YesOrNoEnum.NO.getCode());
        queryWrapper.eq(ShortLink::getDelFlag, YesOrNoEnum.YES.getCode());
        IPage<ShortLink> shortLinkIPage = baseMapper.selectPage(page, queryWrapper);
        return shortLinkIPage;
    }

    public void removeByConditions(RecycleBinRemoveParam reqDTO) {
        lambdaUpdate()
                .eq(ShortLink::getUsername,reqDTO.getUsername())
                .eq(ShortLink::getShortUri,getShortUri(reqDTO.getFullShortUrl()))
                .set(ShortLink::getEnableStatus,YesOrNoEnum.NO.getCode())
                .set(ShortLink::getDelFlag,YesOrNoEnum.NO.getCode())
                .update();
    }

    private String getShortUri(String fullShortUrl){
        return StrUtil.subAfter(fullShortUrl, '/', true);
    }
}
