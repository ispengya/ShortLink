package com.ispengya.shortlink.project.controller;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.service.impl.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final UrlService urlService;

    /**
     * 根据 URL 获取对应网站的标题
     */
    @GetMapping("/api/short-link/title")
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