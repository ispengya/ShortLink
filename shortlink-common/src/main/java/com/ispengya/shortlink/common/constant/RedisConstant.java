package com.ispengya.shortlink.common.constant;

/**
 * @author ispengya
 * @date 2023/11/20 16:54
 */
public class RedisConstant {
    //======================锁前缀===================
    /**
     * 用户注册
     */
    public static final String LOCK_USER_REGISTER_PRE_KEY="short-link:lock:register:";
    /**
     * 获取原始链接
     */
    public static final String LOCK_GET_ORIGIN_LINK_PRE_KEY="short-link:lock:goto:";
    /**
     * 短链接修改分组 ID 锁前缀 Key
     */
    public static final String LOCK_GID_UPDATE_KEY = "short-link:lock:update-gid:%s";



    //====================管理模块=======================
    /**
     * 存储用户信息
     */
    public static final String ADMIN_LOGIN_TOKEN_PRE_KEY ="short-link:admin:token:";


    //=========================项目模块=====================
    /**
     * 原始短链接路由
     */
    public static final String LINK_GOTO_PRE_KEY="short-link:project:goto:";
    /**
     * 防止缓存null
     */
    public static final String LINK_GOTO_IS_NULL_PRE_KEY="short-link:project:goto-is-null:";

    /**
     * 创建短链接锁标识
     */
    public static final String SHORT_LINK_CREATE_LOCK_KEY = "short-link:lock:create";


}
