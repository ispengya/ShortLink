package com.ispengya.shortlink.admin.service.impl;

import com.ispengya.shortlink.admin.dao.UserDao;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoDTO;
import com.ispengya.shortlink.admin.domain.entity.User;
import com.ispengya.shortlink.admin.service.UserService;
import com.ispengya.shortlink.admin.service.converter.BeanConverter;
import com.ispengya.shortlink.common.errorcode.BaseErrorCode;
import com.ispengya.shortlink.common.exception.ClientException;
import com.ispengya.shortlink.common.util.Assert;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author 86151
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2023-11-13 21:01:06
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDao userDao;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Override
    public UserInfoDTO getUserByUserName(String username) {
        Assert.notBlank(username,"用户名不能为空");
        User user = userDao.getUserByUserName(username);
        if (Objects.isNull(user)){
            throw new ClientException(BaseErrorCode.USER_NOT_EXIST);
        }
        return BeanConverter.CONVERTER.converterUserInfo(user);
    }

    @Override
    public boolean hasUser(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }
}




