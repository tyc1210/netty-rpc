package com.tyc.provider.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 当连接断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("断开连接");
    }

    // 发生异常断开
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("连接异常断开");
        super.exceptionCaught(ctx, cause);
    }

}
