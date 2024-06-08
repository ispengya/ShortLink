package com.ispengya.shortlink.admin.common.dubbo;

import com.ispengya.shortlink.admin.common.biz.UserContext;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

@Activate(group = {CommonConstants.CONSUMER})
public class DubboConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String username = UserContext.getUsername();
        String userId = UserContext.getUserId();
        invocation.setAttachment("userId",userId);
        invocation.setAttachment("username",username);
        return invoker.invoke(invocation);
    }
}