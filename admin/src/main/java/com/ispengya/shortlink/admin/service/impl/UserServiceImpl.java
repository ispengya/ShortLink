package com.ispengya.shortlink.admin.service.impl;

import com.ispengya.shortlink.admin.dao.UserDao;
import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.domain.entity.User;
import com.ispengya.shortlink.admin.service.UserService;
import com.ispengya.shortlink.admin.service.converter.BeanConverter;
import com.ispengya.shortlink.common.constant.RedisConstant;
import com.ispengya.shortlink.common.errorcode.BaseErrorCode;
import com.ispengya.shortlink.common.exception.ClientException;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;

    @Override
    public UserInfoRespDTO getUserByUserName(String username) {
        User user = userDao.getUserByUserName(username);
        if (Objects.isNull(user)){
            throw new ClientException(BaseErrorCode.USER_NOT_EXIST);
        }
        return BeanConverter.CONVERTER.converterUserInfo(user);
    }

    @Override
    public boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO userRegisterReqDTO) {
        AssertUtil.isFalse(hasUserName(userRegisterReqDTO.getUsername()),"用户名已经存在");
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_USER_REGISTER_PRE_KEY + userRegisterReqDTO.getUsername());
        if (lock.tryLock()){
            try {
                User insert = BeanConverter.CONVERTER.converterUser(userRegisterReqDTO);
                boolean save = userDao.save(insert);
                if (!save){
                    throw new ServiceException(BaseErrorCode.USER_REGISTER_ERROR);
                }
                //存入布隆过滤器
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
            } catch (ServiceException e) {
                throw new ServiceException(BaseErrorCode.USER_REGISTER_ERROR);
            } finally {
                lock.unlock();
            }
        }else {
            throw new ServiceException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
    }
}




