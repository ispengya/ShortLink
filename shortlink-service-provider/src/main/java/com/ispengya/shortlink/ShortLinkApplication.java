package com.ispengya.shortlink;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import com.ispengya.travel.frameworks.starter.cache.toolkit.RedisUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 短链接应用
 */
@SpringBootApplication
@MapperScan("com.ispengya.shortlink.*.mapper")
@EnableDubbo
public class ShortLinkApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkApplication.class, args);
        RedisUtils.stringRedisTemplate=context.getBean(StringRedisTemplate.class);
    }
}
