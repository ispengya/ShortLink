package com.ispengya.shortlink.project.mq.producer;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ispengya.shortlink.common.constant.MQConstant;
import static com.ispengya.shortlink.common.constant.ShortLinkConstant.SHORT_LINK_STATS_UIP_KEY;
import static com.ispengya.shortlink.common.constant.ShortLinkConstant.SHORT_LINK_STATS_UV_KEY;
import com.ispengya.shortlink.project.domain.LinkStatsMQDTO;
import com.ispengya.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 韩志鹏
 * @Description
 * @Date 创建于 2024/6/9 11:06
 */
@Component
public class LinkStatsProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    public void sendMsg(String fullShortUrl,ServletRequest request,
                        ServletResponse response){
        LinkStatsMQDTO linkStatsMQDTO = buildLinkStatsRecordAndSetUser(fullShortUrl, request, response);
        Message<LinkStatsMQDTO> build = MessageBuilder.withPayload(linkStatsMQDTO).build();
        rocketMQTemplate.send(MQConstant.SHORT_LINK_STATS_TOPIC, build);
    }

    public LinkStatsMQDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request,
                                                         ServletResponse response) {
        //标志uv
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            uv.set(UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get());
        };
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        uv.set(each);
                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }
        String remoteAddr = LinkUtil.getActualIp(((HttpServletRequest) request));
        String os = LinkUtil.getOs(((HttpServletRequest) request));
        String browser = LinkUtil.getBrowser(((HttpServletRequest) request));
        String device = LinkUtil.getDevice(((HttpServletRequest) request));
        String network = LinkUtil.getNetwork(((HttpServletRequest) request));
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + fullShortUrl, remoteAddr);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        //TODO
        String keys = IdUtil.getSnowflake(1, 1).nextIdStr();
        return LinkStatsMQDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(uv.get())
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .keys(keys)
                .currentDate(new Date())
                .statsLocaleAmapKey(statsLocaleAmapKey)
                .build();
    }
}
