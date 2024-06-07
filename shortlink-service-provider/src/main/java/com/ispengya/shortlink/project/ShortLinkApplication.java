package com.ispengya.shortlink.project;

import com.ispengya.travel.frameworks.starter.cache.toolkit.RedisUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 短链接应用
 */
@SpringBootApplication
@MapperScan("com.ispengya.shortlink.project.mapper")
public class ShortLinkApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkApplication.class, args);
        RedisUtils.stringRedisTemplate=context.getBean(StringRedisTemplate.class);
    }
}
