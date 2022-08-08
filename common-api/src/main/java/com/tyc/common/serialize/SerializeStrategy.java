package com.tyc.common.serialize;

import com.tyc.common.model.Message;

/**
 * 序列化策略类
 */
public interface SerializeStrategy<T extends Message> {
    /**
     * 反序列化
     */
    Message deserialize(byte[] data,Class<T> clazz);

    /**
     * 序列化
     */
    byte[] serialize(Message message);
}
