package com.tyc.common.model;

public enum  MessageType {
    REQUEST("request",(byte)0),
    RESPONSE("response",(byte)1),
    PING("ping",(byte)2),
    PONG("pong",(byte)3);
    private String msg;
    private Byte code;

    MessageType(String msg, Byte code) {
        this.msg = msg;
        this.code = code;
    }

    public Byte getCode() {
        return code;
    }

    MessageType(String msg) {
        this.msg = msg;
    }
}
