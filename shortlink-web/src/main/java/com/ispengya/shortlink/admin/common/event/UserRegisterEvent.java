package com.ispengya.shortlink.admin.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author ispengya
 * @date 2023/11/26 15:35
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private String username;

    public UserRegisterEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
