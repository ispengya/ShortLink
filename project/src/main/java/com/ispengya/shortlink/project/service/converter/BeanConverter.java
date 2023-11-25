package com.ispengya.shortlink.project.service.converter;

import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author ispengya
 * @date 2023/11/25 17:53
 */
@Mapper
public interface BeanConverter {

    BeanConverter CONVERTER = Mappers.getMapper(BeanConverter.class);

    ShortLinkRespDTO converterLink1(ShortLink shortLink);

}
