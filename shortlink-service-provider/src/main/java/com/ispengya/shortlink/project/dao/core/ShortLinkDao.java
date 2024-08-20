package com.ispengya.shortlink.project.dao.core;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
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
public class ShortLinkDao extends ServiceImpl<ShortLinkMapper, ShortLinkDO> {

    private final ShortLinkMapper shortLinkMapper;

    public IPage<ShortLinkDO> pageLinkList(ShortLinkPageParam shortLinkPageParam) {
//        IPage<ShortLinkDO> page = new Page<>(shortLinkPageParam.getCurrent(), shortLinkPageParam.getSize());
//        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShortLinkDO::getGid, shortLinkPageParam.getGid());
//        queryWrapper.eq(ShortLinkDO::getUsername, shortLinkPageParam.getUsername());
//        queryWrapper.eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.YES.getCode());
//        queryWrapper.eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode());
//        return baseMapper.selectPage(page, queryWrapper);
        IPage<ShortLinkDO> page = new Page<>(shortLinkPageParam.getCurrent(), shortLinkPageParam.getSize());
        return shortLinkMapper.pageLink(page, shortLinkPageParam);
    }

    public List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(List<String> requestParam, String username) {
        return shortLinkMapper.getGroupLinkCount(requestParam, username);
    }

    public ShortLinkDO getOneByConditions(String username, String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLinkDO::getUsername, username)
                .eq(ShortLinkDO::getShortUri, getShortUri(fullShortUrl))
                .eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.YES.getCode())
                .eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .one();
    }

    public ShortLinkDO getOneInRecycle(String username, String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLinkDO::getUsername, username)
                .eq(ShortLinkDO::getShortUri, getShortUri(fullShortUrl))
                .eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.NO.getCode())
                .eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .one();
    }

    public void updateByConditions(ShortLinkDO oldLink) {
        lambdaUpdate()
                .eq(ShortLinkDO::getUsername, oldLink.getUsername())
                .eq(ShortLinkDO::getShortUri, oldLink.getShortUri())
                .eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.YES.getCode())
                .eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public void saveRecycle(ShortLinkDO oldLink) {
        lambdaUpdate()
                .eq(ShortLinkDO::getUsername, oldLink.getUsername())
                .eq(ShortLinkDO::getShortUri, getShortUri(oldLink.getFullShortUrl()))
                .eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.YES.getCode())
                .eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public void recoverInRecycle(ShortLinkDO oldLink) {
        lambdaUpdate()
                .eq(ShortLinkDO::getUsername, oldLink.getUsername())
                .eq(ShortLinkDO::getShortUri, getShortUri(oldLink.getFullShortUrl()))
                .eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .update(oldLink);
    }

    public IPage<ShortLinkDO> pageRecycleOfLink(RecycleBinPageParam reqDTO) {
        IPage<ShortLinkDO> page = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getUsername, reqDTO.getUsername());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, YesOrNoEnum.NO.getCode());
        queryWrapper.eq(ShortLinkDO::getDelFlag, YesOrNoEnum.YES.getCode());
        IPage<ShortLinkDO> shortLinkIPage = baseMapper.selectPage(page, queryWrapper);
        return shortLinkIPage;
    }

    public void removeByConditions(RecycleBinRemoveParam reqDTO) {
        lambdaUpdate()
                .eq(ShortLinkDO::getUsername, reqDTO.getUsername())
                .eq(ShortLinkDO::getShortUri, getShortUri(reqDTO.getFullShortUrl()))
                .set(ShortLinkDO::getEnableStatus, YesOrNoEnum.NO.getCode())
                .set(ShortLinkDO::getDelFlag, YesOrNoEnum.NO.getCode())
                .update();
    }

    private String getShortUri(String fullShortUrl) {
        return StrUtil.subAfter(fullShortUrl, '/', true);
    }

    public void incrementStats(String username, String fullShortUrl, Integer totalPv, Integer totalUv,
                               Integer totalUip) {
        String shortUri = getShortUri(fullShortUrl);
        shortLinkMapper.incrementStats(username,shortUri,totalPv,totalUv,totalUip);
    }
}
