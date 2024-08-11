package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.service.UrlDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/short-link/admin/v1")
public class UrlTitleController {
    @DubboReference
    private UrlDubboService urlService;

    /**
     * 根据 URL 获取对应网站的标题
     */
    @GetMapping("/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return Results.success(urlService.getTitleByUrl(url));
    }

    /**
     * 根据 URL 获取对应网站的favicon
     */
    @GetMapping("/api/short-link/favicon")
    public Result<String> getFaviconByUrl(@RequestParam("url") String url) {
        return Results.success(urlService.getFavicon(url));
    }
}