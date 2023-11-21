package com.ispengya.shortlink.common.enums;

import lombok.Getter;

/**
 * @author ispengya
 * @date 2023/11/21 16:55
 */
@Getter
public enum YesOrNoEnum {
    YES(0),
    NO(1)
    ;

    private Integer code;

    YesOrNoEnum(int code) {
        this.code=code;
    }
}
