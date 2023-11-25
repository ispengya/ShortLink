package com.ispengya.shortlink.project.service.converter;

import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;

/**
 * @author ispengya
 * @date 2023/11/25 16:18
 */
public class ShortLinkConverter {

    public static final int DATA_INIT = 0;

    public static ShortLink buildShortLink(String shortUri, String fullShortUrl, ShortLinkCreateReqDTO shortLinkCreateReqDTO, String username, String favicon) {
        return ShortLink.builder()
                .gid(shortLinkCreateReqDTO.getGid())
                .username(username)
                .domain(shortLinkCreateReqDTO.getDomain())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .createdType(shortLinkCreateReqDTO.getCreatedType())
                .validDateType(shortLinkCreateReqDTO.getValidDateType())
                .validDate(shortLinkCreateReqDTO.getValidDate())
                .describe(shortLinkCreateReqDTO.getDescribe())
                .shortUri(shortUri)
                .enableStatus(DATA_INIT)
                .totalPv(DATA_INIT)
                .totalUv(DATA_INIT)
                .totalUip(DATA_INIT)
                .fullShortUrl(fullShortUrl)
                .favicon(favicon)
                .build();
    }
}
