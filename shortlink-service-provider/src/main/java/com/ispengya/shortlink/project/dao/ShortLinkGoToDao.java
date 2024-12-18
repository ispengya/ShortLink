package com.ispengya.shortlink.project.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.project.domain.ShortLinkGotoDO;
import com.ispengya.shortlink.project.mapper.ShortLinkGotoMapper;
import org.springframework.stereotype.Repository;

/**
 * @author ispengya
 * @date 2023/12/9 10:01
 */
@Repository
public class ShortLinkGoToDao extends ServiceImpl<ShortLinkGotoMapper, ShortLinkGotoDO> {
    public ShortLinkGotoDO getByFullShortUrl(String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
                .eq(ShortLinkGotoDO::getDelFlag, YesOrNoEnum.YES.getCode())
                .one();
    }

    public ShortLinkGotoDO getByFullShortUrlWithOut(String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
                .one();
    }

    public void updateStatus(String username, String fullShortUrl,YesOrNoEnum yesOrNoEnum) {
        lambdaUpdate()
                .eq(ShortLinkGotoDO::getUsername,username)
                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
                .set(ShortLinkGotoDO::getDelFlag,yesOrNoEnum.getCode())
                .update();
    }

    public ShortLinkGotoDO getByFullShortUrlAndUserName(String fullShortUrl, String username) {
        return lambdaQuery()
                .eq(ShortLinkGotoDO::getUsername,username)
                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
                .one();
    }

    public void deleteByConditions(ShortLinkGotoDO shortLinkGoTo) {
        LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, shortLinkGoTo.getFullShortUrl())
                .eq(ShortLinkGotoDO::getUsername,shortLinkGoTo.getUsername())
                .eq(ShortLinkGotoDO::getGid, shortLinkGoTo.getGid());
        baseMapper.delete(linkGotoQueryWrapper);
    }
}
