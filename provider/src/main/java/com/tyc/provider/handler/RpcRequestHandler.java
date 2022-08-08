package com.tyc.provider.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.provider.cache.MethodCache;
import com.tyc.provider.util.BeanUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 专门处理 RpcRequest 类型的消息
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        String methodName = rpcRequest.getMethodName();
        MethodCache.MethodContext methodContext = MethodCache.methodMap.get(methodName);
        if(null == methodContext){
            log.warn("请求方法不存在或未暴露:{}",methodName);
            return;
        }
        Method method = methodContext.getMethod();
        Object[] rpcRequestArgs = rpcRequest.getArgs();
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 类型转换
        Object[] params = new Object[rpcRequestArgs.length];
        for (int i = 0; i < rpcRequestArgs.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object arg = rpcRequestArgs[i];
            if(parameterType.isAssignableFrom(List.class)){
                params[i] = JSONArray.parseArray(JSONArray.toJSONString(arg),parameterType);
            }else if(parameterType.isAssignableFrom(String.class)){
                params[i] = arg;
            }else if(parameterType.isAssignableFrom(Long.class)){
                params[i] = JSONObject.parseObject(JSONObject.toJSONString(arg),parameterType);
            }
        }

        Object obj = methodContext.getMethod().invoke(BeanUtils.getBeanByName(methodContext.getBeanName()), params);
        String data = JSONObject.toJSONString(obj);
        RpcResult rpcResult = new RpcResult(0,rpcRequest.getId(),data);
        log.debug("返回客户端执行结果:{}",JSONObject.toJSONString(rpcResult));
        ctx.channel().write(rpcResult);
        ctx.channel().unsafe().flush();
    }
}
