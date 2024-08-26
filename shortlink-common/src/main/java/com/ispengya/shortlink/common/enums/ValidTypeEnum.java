package com.ispengya.shortlink.common.enums;

import lombok.Getter;

/**
 * @author ispengya
 * @date 2023/12/9 11:52
 */
@Getter
public enum ValidTypeEnum {
    FOREVER(0, "永久"),
    CUSTOM(1, "自定义");


    private Integer type;
    private String desc;

    ValidTypeEnum(Integer type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
