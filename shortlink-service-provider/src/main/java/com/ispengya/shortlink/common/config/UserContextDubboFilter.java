package com.ispengya.shortlink.common.config;

import com.alibaba.dubbo.common.Constants;
import com.ispengya.shortlink.common.biz.user.UserContext;
import com.ispengya.shortlink.common.biz.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

@Slf4j
//1 使用注解开启 拦截器
@Activate(group = {Constants.PROVIDER})
public class UserContextDubboFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //获取用户信息
        String userId = invocation.getAttachment("userId");
        String username = invocation.getAttachment("username");
        log.info("userId : {}, username : {}", userId, username);
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(userId)
                .username(username)
                .build();
        UserContext.setUser(userInfoDTO);
        Result result = invoker.invoke(invocation);
        UserContext.removeUser();
        return result;
    }
}
