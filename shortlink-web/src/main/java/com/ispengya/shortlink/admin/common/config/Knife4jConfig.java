package com.ispengya.shortlink.admin.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi adminApi() {// 创建了一个api接口的分组
        return GroupedOpenApi.builder()
                .group("link-api")
                .pathsToMatch("/**")
                .pathsToExclude("/test","/api/short-link/title","/api/short-link/favicon","/page/notfound")// 接口请求路径规则
                .build();
    }

//    @Bean
//    public OpenAPI openAPI(){
//        return new OpenAPI()
//                .info(new Info()
//                        .title("链易短-api文档")
//                        .description("仓库地址：https://github.com/ispengya/ShortLink")
//                        .version("1.0")
//                        .contact(new Contact().name("zaizaige").email("hanzhipeng151@163.com"))
//                );
//
//    }
}