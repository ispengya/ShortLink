package com.ispengya.shortlink.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ispengya.shortlink.common.exception.ServiceException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 */
public class AssertUtil {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ServiceException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new ServiceException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (Objects.nonNull(object)) {
            throw new ServiceException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new ServiceException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (CollUtil.isEmpty(collection)) {
            throw new ServiceException(message);
        }
    }


    public static void notEmpty(Map<?, ?> map, String message) {
        if (CollUtil.isEmpty(map)) {
            throw new ServiceException(message);
        }
    }


    public static void notBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            throw new ServiceException(message);
        }
    }

    public static void hasText(String text, String message) {
        if (StrUtil.hasBlank(text)) {
            throw new ServiceException(message);
        }
    }

}
