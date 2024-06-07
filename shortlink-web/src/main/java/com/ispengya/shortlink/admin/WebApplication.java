package com.ispengya.shortlink.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ispengya
 * @date 2023/11/11 21:28
 */
@SpringBootApplication
@MapperScan("com.ispengya.shortlink.admin.mapper")
@EnableDubbo
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class,args);
    }
}
