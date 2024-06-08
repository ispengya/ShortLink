package com.ispengya.shortlink.project.service;

/**
 * @author 韩志鹏
 * @Description
 * @Date 创建于 2024/6/8 20:33
 */
public interface UrlDubboService {
    String getTitleByUrl(String url);
    String getFavicon(String url);
}
