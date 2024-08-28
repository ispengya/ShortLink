package com.ispengya.shortlink;

import com.ispengya.shortlink.project.service.ShortLinkDubboService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.SneakyThrows;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 短链接应用
 */
@SpringBootApplication
@MapperScan("com.ispengya.shortlink.*.mapper")
@EnableDubbo
@Controller
public class ShortLinkApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkApplication.class, args);
    }

    @Autowired
    private ShortLinkDubboService shortLinkDubboService;

    /**
     * 短链接跳转原始链接
     */
    @GetMapping("/{short-uri}")
    @SneakyThrows
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) {
        shortLinkDubboService.jumpUrlV1(shortUri, request, response);
    }
}
