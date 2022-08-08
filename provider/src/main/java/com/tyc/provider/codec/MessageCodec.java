package com.tyc.provider.codec;

import com.tyc.common.model.Message;
import com.tyc.common.model.MessageType;
import com.tyc.common.serialize.SerializeStrategyContext;
import com.tyc.provider.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 自定义协议
 * /*
 * +---------------------------------------------------------------+
 * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
 * +---------------------------------------------------------------+
 * | 状态 1byte |  填充2byte  |     消息id 4byte     |      数据长度 4byte     |
 * +---------------------------------------------------------------+
 * |                   数据内容 （长度不定）           |
 * +---------------------------------------------------------------+
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-03 14:51:30
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        // 写魔数 是通信双方协商的一个暗号 增强系统安全性
        buffer.writeBytes("yc".getBytes());
        // 协议版本
        buffer.writeByte(1);
        // 序列化方式
        buffer.writeByte(msg.getSerializeType().getCode());
        // 报文类型 有请求、响应、心跳类型等
        buffer.writeByte(msg.getMessageType().getCode());
        // 状态 成功失败等
        buffer.writeByte(0);
        // 填充
        buffer.writeBytes(new byte[2]);
        // 消息id
        buffer.writeInt(msg.getMessageId());
        // 根据序列化方式选择不同处理方式
        SerializeStrategyContext serializeStrategyContext = new SerializeStrategyContext(msg.getSerializeType().getCode());
        byte[] msgBytes = serializeStrategyContext.serialize(msg);
        if(Objects.isNull(msgBytes)){
            log.warn("无效的序列化方式：{}",msg.getSerializeType().getMsg());
            return;
        }
        // 写入长度
        buffer.writeInt(msgBytes.length);
        // 写入数据
        buffer.writeBytes(msgBytes);
        log.info("MessageCodec encode result");
        LogUtil.log(buffer);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] magicNum = new byte[2];
        byteBuf.readBytes(magicNum);
        byte protocolVersion = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        byte state = byteBuf.readByte();
        byteBuf.readBytes(2);
        int id = byteBuf.readInt();
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        SerializeStrategyContext serializeStrategyContext = new SerializeStrategyContext(serializeType);
        Message message = serializeStrategyContext.deSerialize(data, MessageType.getByCode(messageType));
        if(Objects.isNull(message)){
            log.warn("无效的序列化方式:{}",serializeType);
            return;
        }
        log.info("解码结果===> 魔数:{},协议版本:{},序列化方式:{},报文类型:{},状态:{},消息id:{},消息长度:{},消息：{}"
                ,new String(magicNum),protocolVersion,serializeType,message,state,id,length,message.toString());
        out.add(message);
    }
}
