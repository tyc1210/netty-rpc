package com.tyc.consumer.handler;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RequestFuture;
import com.tyc.common.model.RpcResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcResultHandler extends SimpleChannelInboundHandler<RpcResult> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResult rpcResult) throws Exception {
        log.info("客户端收到消息：{}", JSONObject.toJSONString(rpcResult));
        RequestFuture.rpcRequestMap.get(rpcResult.getId()).getRequestFuture().receive(rpcResult);
    }
}
