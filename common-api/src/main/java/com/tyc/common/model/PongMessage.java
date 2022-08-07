package com.tyc.common.model;

public class PongMessage extends Message{
    public SerializeType getSerializeType() {
        return SerializeType.JSON;
    }

    public MessageType getMessageType() {
        return MessageType.PONG;
    }

    public Integer getMessageId() {
        return -1;
    }
}
