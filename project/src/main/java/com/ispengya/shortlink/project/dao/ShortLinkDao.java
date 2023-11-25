package com.ispengya.shortlink.project.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import com.ispengya.shortlink.project.mapper.ShortLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:17
 */
@Repository
public class ShortLinkDao extends ServiceImpl<ShortLinkMapper, ShortLink> {

    @Autowired
    private ShortLinkMapper shortLinkMapper;



    public List<ShortLink> get() {
        return lambdaQuery()
                .eq(ShortLink::getUsername, "ispengya")
                .eq(ShortLink::getGid, "test")
                .list();
    }

    public IPage<ShortLink> pageLink(ShortLinkPageReq shortLinkPageReq) {
        IPage<ShortLink> page = new Page<>(shortLinkPageReq.getCurrent(), shortLinkPageReq.getPageSize());
        LambdaQueryWrapper<ShortLink> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLink::getGid, shortLinkPageReq.getGid());
        queryWrapper.eq(ShortLink::getUsername, shortLinkPageReq.getUsername());
        queryWrapper.eq(ShortLink::getEnableStatus, YesOrNoEnum.YES.getCode());
        IPage<ShortLink> shortLinkIPage = baseMapper.selectPage(page, queryWrapper);
        return shortLinkIPage;
    }

    public List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(List<String> requestParam, String username) {
        return shortLinkMapper.getGroupLinkCount(requestParam, username);
    }
}
