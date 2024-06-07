package com.ispengya.shortlink.project.common.enums;

import lombok.Getter;

/**
 * @author ispengya
 * @date 2023/12/9 11:52
 */
@Getter
public enum ValidTypeEnum {
    FOREVER(1, "永久"),
    CUSTOM(0, "自定义");


    private Integer type;
    private String desc;

    ValidTypeEnum(Integer type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
