package com.ispengya.shortlink.admin.common.interceptor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ispengya.shortlink.admin.common.biz.UserContext;
import com.ispengya.shortlink.admin.common.biz.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 韩志鹏
 * @Description
 * @Date 创建于 2024/6/8 17:35
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    private StringRedisTemplate stringRedisTemplate;

    public UserLoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if (StringUtils.hasText(username) && StringUtils.hasText(token)){
            String userInfoStr = stringRedisTemplate.opsForValue().get("short-link:admin:token:" + username);
            if (StringUtils.hasText(userInfoStr)){
                JSONObject userInfoJsonObject = JSON.parseObject(userInfoStr);
                String userId = userInfoJsonObject.getString("userId");
                String username1 = URLEncoder.encode(userInfoJsonObject.getString("username"), StandardCharsets.UTF_8);
                UserInfoDTO userInfoDTO = UserInfoDTO.builder().userId(userId).username(username1).build();
                UserContext.setUser(userInfoDTO);
                return true;
            }else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(GatewayErrorResult.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Token validation error")
                        .build()));
                return false;
            }
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(GatewayErrorResult.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Token validation error")
                    .build()));
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
