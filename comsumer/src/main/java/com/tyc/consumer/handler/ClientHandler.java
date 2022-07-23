package com.tyc.consumer.handler;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RequestFuture;
import com.tyc.common.model.RpcResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:12:19
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResult rpcResult = JSONObject.parseObject(msg.toString(), RpcResult.class);
        log.info("客户端收到消息：{}",JSONObject.toJSONString(rpcResult));
        RequestFuture.rpcRequestMap.get(rpcResult.getId()).getRequestFuture().receive(rpcResult);
    }
}
