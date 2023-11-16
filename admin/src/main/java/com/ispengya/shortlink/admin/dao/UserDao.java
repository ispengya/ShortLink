package com.ispengya.shortlink.admin.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.admin.domain.entity.User;
import com.ispengya.shortlink.admin.mapper.UserMapper;
import org.springframework.stereotype.Repository;

/**
 * @author ispengya
 * @date 2023/11/13 21:03
 */
@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {
    public User getUserByUserName(String username) {
        return lambdaQuery()
                .eq(User::getUsername,username)
                .one();
    }
}
