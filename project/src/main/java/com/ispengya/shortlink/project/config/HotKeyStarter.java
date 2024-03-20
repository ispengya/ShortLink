package com.ispengya.shortlink.project.config;

import com.jd.platform.hotkey.client.ClientStarter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class HotKeyStarter {
    @Value("${etcd.server}")
    private String etcd;
    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void init() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName).setEtcdServer(etcd).setCaffeineSize(1000).build();
        starter.startPipeline();
    }


}