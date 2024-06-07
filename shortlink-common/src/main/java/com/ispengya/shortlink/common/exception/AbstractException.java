package com.ispengya.shortlink.common.exception;


import cn.hutool.core.util.StrUtil;
import com.ispengya.shortlink.common.errorcode.IErrorCode;
import lombok.Getter;

import java.util.Optional;

/**
 * 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StrUtil.isNotBlank(message) ? message : null).orElse(errorCode.message());
    }

}
