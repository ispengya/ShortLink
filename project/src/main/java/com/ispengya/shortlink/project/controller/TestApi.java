package com.ispengya.shortlink.project.controller;

import com.ispengya.travel.frameworks.starter.cache.toolkit.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ispengya
 * @date 2024/3/23 14:57
 */
@RestController
public class TestApi {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/hello")
    public String get(){
        RedisUtils.del("1");
        return "OK";
    }

}
