package com.ispengya.shortlink.project.service;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * URL 标题接口实现层
 */
@Service
@DubboService
public class UrlDubboServiceImpl implements UrlDubboService {

    @SneakyThrows
    public String getTitleByUrl(String url) {
        Document urlDocument = getUrlDocument(url);
        if (urlDocument==null){
            return "Undefined";
        }
        String title = urlDocument.title();
        if (StrUtil.isNotBlank(title)){
            return title;
        }else {
            title=urlDocument.getElementsByAttributeValue("property", "og:title").attr("content");
        }
        if (StrUtil.isNotBlank(title)){
            return title;
        }
        return "Undefined";
    }

    @SneakyThrows
    public String getFavicon(String url) {
        Document urlDocument = getUrlDocument(url);
        if (urlDocument==null){
            return "Undefined";
        }
        String image = getImage(url);
        if (StrUtil.isNotBlank(image)){
            return image;
        }else {
            String href = urlDocument.getElementsByAttributeValue("property", "og:image").attr("content");
            image=isConnect(href) ? href : null;
        }
        if (StrUtil.isNotBlank(image)){
            return image;
        }
        return "Undefined";

    }

    //获取网站Document
    private Document getUrlDocument(String matchUrl) {
        try {
            Connection connect = Jsoup.connect(matchUrl);
            //对于github这种超时处理
            connect.timeout(2000);
            return connect.get();
        } catch (Exception e) {
            return null;
        }
    }

    private String getImage(String url) {
//        String image = document.select("link[type=image/x-icon]").attr("href");
//        //如果没有去匹配含有icon属性的logo
//        String href = StrUtil.isEmpty(image) ? document.select("link[rel$=icon]").attr("href") : image;
//        //如果url已经包含了logo
//        if (StrUtil.containsAny(url, "favicon")) {
//            return url;
//        }
//        //如果icon可以直接访问或者包含了http
//        if (isConnect(!StrUtil.startWith(href, "http") ? href="http"+href : href)) {
//            return href;
//        }
//
//        return StrUtil.format("{}/{}", url, StrUtil.removePrefix(href, "/"));
        try {
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                Document document = Jsoup.connect(url).get();
                Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
                if (faviconLink != null) {
                    return faviconLink.attr("abs:href");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static boolean isConnect(String href) {
        //请求地址
        URL url;
        //请求状态码
        int state;
        //下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            //如果成功200，缓存304，移动302都算有效链接，并且不是下载链接
            if ((state == 200 || state == 302 || state == 304) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
