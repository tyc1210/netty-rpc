package com.tyc.common.model;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-03 14:45:52
 */
public class Message implements Serializable {
    private final String data;

    public Message(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data='" + data + '\'' +
                '}';
    }
}
