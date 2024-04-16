package com.ispengya.shortlink.admin.service.impl;

import com.ispengya.shortlink.admin.common.event.UserRegisterEvent;
import com.ispengya.shortlink.admin.dao.UserDao;
import com.ispengya.shortlink.admin.domain.dto.req.UserLoginReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserRegisterReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.UserUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserInfoRespDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.UserLoginRespDTO;
import com.ispengya.shortlink.admin.domain.entity.User;
import com.ispengya.shortlink.admin.service.UserService;
import com.ispengya.shortlink.admin.service.converter.BeanConverter;
import com.ispengya.shortlink.admin.util.JwtUtils;
import com.ispengya.shortlink.admin.util.RedisUtils;
import com.ispengya.shortlink.common.constant.RedisConstant;
import com.ispengya.shortlink.common.errorcode.BaseErrorCode;
import com.ispengya.shortlink.common.exception.ClientException;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
* @author 86151
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2023-11-13 21:01:06
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    public static final int TOKEN_TIMEOUT = 30*60;
    private final UserDao userDao;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final ApplicationEventPublisher applicationEventPublisher;

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
    @Transactional
    public void register(UserRegisterReqDTO userRegisterReqDTO) {
        AssertUtil.isFalse(hasUserName(userRegisterReqDTO.getUsername()),"用户名已经存在");
        //加锁
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_USER_REGISTER_PRE_KEY + userRegisterReqDTO.getUsername());
        if (lock.tryLock()){
            try {
                User insert = BeanConverter.CONVERTER.converterUser1(userRegisterReqDTO);
                boolean save = userDao.save(insert);
                if (!save){
                    throw new ServiceException(BaseErrorCode.USER_REGISTER_ERROR);
                }
                //存入布隆过滤器
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
                //创建默认分组
                applicationEventPublisher.publishEvent(new UserRegisterEvent(this,insert.getUsername()));
            } catch (ServiceException e) {
                throw new ServiceException(BaseErrorCode.USER_REGISTER_ERROR);
            } finally {
                lock.unlock();
            }
        }else {
            throw new ServiceException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
    }

    @Override
    public void updateUserInfo(UserUpdateReqDTO userUpdateReqDTO) {
        //查询用户是否存在
        User oldUser = userDao.getUserByUserName(userUpdateReqDTO.getUsername());
        AssertUtil.notNull(oldUser,"用户不存在");
        //更新用户
        User update = BeanConverter.CONVERTER.converterUser2(userUpdateReqDTO);
        userDao.updateByUserName(update);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        User loginUser = userDao.getUserByUserName(userLoginReqDTO.getUsername());
        AssertUtil.notNull(loginUser,"用户名不存在");
        AssertUtil.isTrue(Objects.equals(loginUser.getPassword(),userLoginReqDTO.getPassword()),"密码错误");
        //生成token
        String token = JwtUtils.createToken(loginUser.getUsername());
        //存入Redis
        RedisUtils.hset(RedisConstant.ADMIN_LOGIN_TOKEN_PRE_KEY +userLoginReqDTO.getUsername(),token, loginUser,TOKEN_TIMEOUT);
        return new UserLoginRespDTO(token);
    }

    @Override
    public void logout(String username, String token) {
        //清楚redis
        RedisUtils.del(RedisConstant.ADMIN_LOGIN_TOKEN_PRE_KEY +username);
    }

    @Override
    public Boolean checkLogin(String username) {
        return RedisUtils.hasKey(RedisConstant.ADMIN_LOGIN_TOKEN_PRE_KEY +username);
    }
}




