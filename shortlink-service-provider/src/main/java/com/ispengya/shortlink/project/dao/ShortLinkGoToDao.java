package com.ispengya.shortlink.project.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.project.domain.ShortLinkGoto;
import com.ispengya.shortlink.project.mapper.ShortLinkGotoMapper;
import org.springframework.stereotype.Repository;

/**
 * @author ispengya
 * @date 2023/12/9 10:01
 */
@Repository
public class ShortLinkGoToDao extends ServiceImpl<ShortLinkGotoMapper, ShortLinkGoto> {
    public ShortLinkGoto getByFullShortUrl(String fullShortUrl) {
        return lambdaQuery()
                .eq(ShortLinkGoto::getFullShortUrl,fullShortUrl)
                .one();
    }
}
