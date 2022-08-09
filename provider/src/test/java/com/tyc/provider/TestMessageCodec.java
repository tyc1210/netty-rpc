package com.tyc.provider;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.Message;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.SerializeType;
import com.tyc.provider.codec.MessageCodec;
import com.tyc.provider.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-03 15:36:21
 */
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec(),
                new LoggingHandler(LogLevel.DEBUG)
        );

        Message message = new RpcRequest(1,"1",new Object[2]);
        ByteBuf buffer = encode(message);
        int size = buffer.readableBytes();
        // 测试解码 半包发送
        ByteBuf slice1 = buffer.slice(0, 10);
        slice1.retain();
        embeddedChannel.writeInbound(slice1);
        ByteBuf slice2 = buffer.slice(10, size-10);
        embeddedChannel.writeInbound(slice2);

        // 测试编码
        embeddedChannel.writeOutbound(message);
    }

    public static ByteBuf encode(Message msg) throws Exception {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("yc".getBytes());
        buffer.writeByte(1);
        buffer.writeByte(SerializeType.JSON.getCode());
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeBytes(new byte[2]);
        buffer.writeInt(1);
        String string = JSONObject.toJSONString(msg);
        byte[] msgBytes = string.getBytes();
        buffer.writeInt(msgBytes.length);
        buffer.writeBytes(msgBytes);
        LogUtil.log(buffer);
        return buffer;
    }


}
