package com.tyc.consumer.handler;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.Message;
import com.tyc.common.model.RpcResult;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tyc
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResultHandler extends SimpleChannelInboundHandler<RpcResult> {
    public static Map<Integer, Promise<RpcResult>> map = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResult rpcResult) throws Exception {
        log.info("客户端收到消息：{}", JSONObject.toJSONString(rpcResult));
        Promise<RpcResult> promise = map.remove(rpcResult.getId());
        if(null != promise){
            if(rpcResult.getCode().equals(0)){
                promise.setSuccess(rpcResult);
            }else {
                promise.setFailure(new RuntimeException(rpcResult.getResultData()));
            }
        }
    }
}
