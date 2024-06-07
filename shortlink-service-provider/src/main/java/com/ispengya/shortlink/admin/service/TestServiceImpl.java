package com.ispengya.shortlink.admin.service;

import com.ispengya.travel.frameworks.starter.cache.core.distributed.DistributedCache;
import com.ispengya.travel.frameworks.starter.cache.core.multistage.MultiStageCache;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 韩志鹏
 * @Description
 * @Date 创建于 2024/6/7 16:42
 */
@DubboService
@Service
public class TestServiceImpl implements TestService{
    @Autowired
    private MultiStageCache multiStageCache;
    @Autowired
    private DistributedCache distributedCache;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void hello() {
        System.out.println("===========================================");
        System.out.println(multiStageCache);
        System.out.println(distributedCache);
        System.out.println(redissonClient);
        System.out.println(stringRedisTemplate);
        System.out.println("===========================================");
    }
}
