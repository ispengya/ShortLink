package com.ispengya.shortlink.common.constant;

public class ShortLinkConstant {

    /**
     * 永久短链接默认缓存有效时间，默认一个月
     */
    public static final long DEFAULT_CACHE_VALID_TIME = 2592000L;

    /**
     * 短链接统计判断是否新用户缓存标识
     */
    public static final String SHORT_LINK_STATS_UV_KEY = "short-link:stats:uv:";

    /**
     * 短链接统计判断是否新 IP 缓存标识
     */
    public static final String SHORT_LINK_STATS_UIP_KEY = "short-link:stats:uip:";

    /**
     * 高德获取地区接口地址
     */
    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";
}