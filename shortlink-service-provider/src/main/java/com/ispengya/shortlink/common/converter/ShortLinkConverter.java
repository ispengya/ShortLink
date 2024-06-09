package com.ispengya.shortlink.common.converter;

import com.ispengya.shortlink.project.domain.ShortLink;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkCreateRespDTO;

/**
 * @author ispengya
 * @date 2023/11/25 16:18
 */
public class ShortLinkConverter {

    public static final int DATA_INIT = 0;

    public static ShortLink buildShortLink(String shortUri, String fullShortUrl, ShortLinkCreateParam shortLinkCreateParam, String username, String favicon) {
        return ShortLink.builder()
                .gid(shortLinkCreateParam.getGid())
                .username(username)
                .domain(shortLinkCreateParam.getDomain())
                .originUrl(shortLinkCreateParam.getOriginUrl())
                .createdType(shortLinkCreateParam.getCreatedType())
                .validDateType(shortLinkCreateParam.getValidDateType())
                .validDate(shortLinkCreateParam.getValidDate())
                .describe(shortLinkCreateParam.getDescribe())
                .shortUri(shortUri)
                .enableStatus(DATA_INIT)
                .totalPv(DATA_INIT)
                .totalUv(DATA_INIT)
                .totalUip(DATA_INIT)
                .fullShortUrl(fullShortUrl)
                .favicon(favicon)
                .build();
    }

    public static ShortLinkCreateRespDTO buildShortLinkCreateResp(ShortLink shortLink, String officialDomain) {
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLink.getFullShortUrl())
                .originUrl(shortLink.getOriginUrl())
                .gid(shortLink.getGid())
                .build();
        //判断domain是否是自定义的
        if (shortLink.getDomain().equals(officialDomain)) {
            //TODO 真正上线是https
            shortLinkCreateRespDTO.setFullShortUrl("http://" + shortLink.getFullShortUrl());
        } else {
            shortLinkCreateRespDTO.setFullShortUrl("http://" + shortLink.getFullShortUrl());
        }
        return shortLinkCreateRespDTO;
    }
}
