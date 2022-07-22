package com.tyc.provider.handler;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.provider.cache.MethodCache;
import com.tyc.provider.util.BeanUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
//        RpcRequest rpcRequest = JSONObject.parseObject(msg.toString(), RpcRequest.class);
//        String methodName = rpcRequest.getMethodName();
//        MethodCache.MethodContext methodContext = MethodCache.methodMap.get(methodName);
//        Object obj = methodContext.getMethod().invoke(BeanUtils.getBeanByName(methodContext.getBeanName()), rpcRequest.getArgs());
//        String data = JSONObject.toJSONString(obj);
//        RpcResult rpcResult = new RpcResult();
//        rpcResult.setResultData(data);
//        rpcResult.setId(rpcRequest.getId());
        ctx.writeAndFlush("111".getBytes());
    }
}
