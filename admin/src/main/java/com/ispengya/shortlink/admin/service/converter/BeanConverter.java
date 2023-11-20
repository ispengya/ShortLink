package com.ispengya.shortlink.admin.service.converter;

import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author ispengya
 * @date 2023/11/16 17:00
 */
@Mapper
public interface BeanConverter {
    BeanConverter CONVERTER = Mappers.getMapper(BeanConverter.class);
    UserInfoRespDTO converterUserInfo(User user);

    User converterUser(UserRegisterReqDTO userRegisterReqDTO);
}
