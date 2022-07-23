package com.tyc.provider.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.provider.cache.MethodCache;
import com.tyc.provider.util.BeanUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:38:17
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("请求信息为：{}",msg.toString());
        // 将请求解析
        RpcRequest rpcRequest = JSONObject.parseObject(msg.toString(), RpcRequest.class);
        String methodName = rpcRequest.getMethodName();
        MethodCache.MethodContext methodContext = MethodCache.methodMap.get(methodName);
        if(null == methodContext){
            throw new RuntimeException("no such method");
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
        String result = JSONObject.toJSONString(rpcResult);
        log.info("返回客户端执行结果:{}",result);
//        ctx.writeAndFlush(result);
        ctx.channel().write(result);
        ctx.channel().unsafe().flush();    }
}
