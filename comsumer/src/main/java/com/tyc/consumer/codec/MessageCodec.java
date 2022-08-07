package com.tyc.consumer.codec;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.*;
import com.tyc.consumer.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        if(msg.getSerializeType().getCode() == 0){
            byte[] msgBytes = JSONObject.toJSONString(msg).getBytes();
            // 写入长度
            buffer.writeInt(msgBytes.length);
            // 写入数据
            buffer.writeBytes(msgBytes);
        }else {
         log.error("不支持的序列化方式");
        }
        // 校验字段存放某种校验算法计算报文校验码，校验码用于验证报文的正确性。
//        buffer.writeBytes(new byte[2]);
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
        byte packetType = byteBuf.readByte();
        byte state = byteBuf.readByte();
        byteBuf.readBytes(2);
        int id = byteBuf.readInt();
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        if(MessageType.RESPONSE.getCode() != packetType){
            log.error("无效的响应类型:",packetType);
            return;
        }
        Message message = null;
        if(SerializeType.JSON.getCode() == serializeType && packetType == MessageType.RESPONSE.getCode()){
            message = JSONObject.parseObject(new String(data), RpcResult.class);
        }else if(SerializeType.JSON.getCode() == serializeType && packetType == MessageType.PONG.getCode()){
            message = JSONObject.parseObject(new String(data), PongMessage.class);
        }else {
            log.error("无效的系列化方式");
            return;
        }
        log.info("解码结果===> 魔数:{},协议版本:{},序列化方式:{},报文类型:{},状态:{},消息id:{},消息长度:{},消息：{}"
                ,new String(magicNum),protocolVersion,serializeType,packetType,state,id,length,message.toString());
        out.add(message);
    }
}
