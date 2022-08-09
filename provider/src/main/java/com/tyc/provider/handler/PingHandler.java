package com.tyc.provider.handler;

import com.tyc.common.model.PingMessage;
import com.tyc.common.model.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理心跳消息
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-09 17:18:48
 */
public class PingHandler extends SimpleChannelInboundHandler<PingMessage> {
    private final  PongMessage pongMessage = new PongMessage();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        ctx.writeAndFlush(pongMessage);
    }
}
