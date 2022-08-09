package com.tyc.consumer.handler;

import com.tyc.common.model.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳监测处理
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-09 16:44:52
 */
@Slf4j
public class HeartBeatHandler extends ChannelDuplexHandler {
    private final PingMessage pingMessage = new PingMessage();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.WRITER_IDLE){
                log.debug("触发写空闲事件，发送心跳消息");
                ctx.writeAndFlush(pingMessage);
            }
            if(event.state() == IdleState.READER_IDLE){
                log.debug("触发读空闲事件，主动断开重连");
                ctx.channel().close();
            }
        }
    }
}
