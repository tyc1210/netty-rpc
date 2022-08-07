package com.tyc.common.model;

public class PingMessage extends Message{
    public SerializeType getSerializeType() {
        return SerializeType.JSON;
    }

    public MessageType getMessageType() {
        return MessageType.PING;
    }

    public Integer getMessageId() {
        return -1;
    }
}
