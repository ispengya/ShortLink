package com.ispengya.shortlink.common.converter;

import com.ispengya.shortlink.admin.domain.Group;
import com.ispengya.shortlink.admin.domain.User;
import com.ispengya.shortlink.admin.dto.request.UserRegisterParam;
import com.ispengya.shortlink.admin.dto.request.UserUpdateParam;
import com.ispengya.shortlink.admin.dto.response.GroupListRespDTO;
import com.ispengya.shortlink.admin.dto.response.UserInfoRespDTO;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author ispengya
 * @date 2023/11/16 17:00
 */
@Mapper
public interface BeanConverter {
    /**
     * admin
     */
    BeanConverter CONVERTER = Mappers.getMapper(BeanConverter.class);

    UserInfoRespDTO converterUserInfo(User user);

    User converterUser1(UserRegisterParam userRegisterParam);

    User converterUser2(UserUpdateParam userUpdateParam);

    GroupListRespDTO converterGroup1(Group group);

    /**
     * project
     */
    ShortLinkRespDTO converterLink1(ShortLinkDO shortLinkDO);
}
