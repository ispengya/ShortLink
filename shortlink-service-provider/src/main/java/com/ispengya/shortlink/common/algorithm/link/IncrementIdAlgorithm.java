package com.ispengya.shortlink.common.algorithm.link;

import com.ispengya.shortlink.common.util.HttpUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @author: hanzhipeng
 * @create: 2024-12-18 10:37
 **/
@Component
public class IncrementIdAlgorithm implements GenerateService , InitializingBean {

    @Value("${link.increment.domain}")
    private String incrementDomain;

    @Value("${link.increment.bizTag}")
    private String incrementBizTag;

    public static LinkedBlockingQueue<Long> idQueue = new LinkedBlockingQueue<>(10);

    @Override
    public String generateShortUrl(String longUrl) throws InterruptedException {
        return toBase62(idQueue.take());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Executors.newSingleThreadExecutor().execute(()->{
            while (true){
                long id = Long.parseLong(HttpUtils.httpGet(incrementDomain + incrementBizTag));
                try {
                    idQueue.put(id);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // 定义62进制字符集
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 将 long 转换为 62 进制字符串
     *
     * @param value 需要转换的 long 值
     * @return 转换后的 62 进制字符串
     */
    public static String toBase62(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative.");
        }
        StringBuilder result = new StringBuilder();
        do {
            int remainder = (int) (value % 62);
            result.append(BASE62[remainder]);
            value /= 62;
        } while (value > 0);
        return result.reverse().toString();
    }


}
