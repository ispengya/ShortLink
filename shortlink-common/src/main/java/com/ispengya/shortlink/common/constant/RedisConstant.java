package com.ispengya.shortlink.common.constant;

/**
 * @author ispengya
 * @date 2023/11/20 16:54
 */
public class RedisConstant {
    //锁前缀
    public static final String LOCK_USER_REGISTER_PRE_KEY="short-link:lock:register:";
    public static final String LOCK_GET_ORIGIN_LINK_PRE_KEY="short-link:lock:register:";

    //管理模块
    public static final String ADMIN_LOGIN_TOKEN_PRE_KEY ="short-link:admin:token:";

    //项目模块
    public static final String LINK_GOTO_PRE_KEY="short-link:project:goto:";
    public static final String LINK_GOTO_IS_NULL_PRE_KEY="short-link:project:goto-is-null:";

}
