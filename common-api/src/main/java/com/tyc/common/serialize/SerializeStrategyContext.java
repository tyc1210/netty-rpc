package com.tyc.common.serialize;

import com.tyc.common.model.Message;
import com.tyc.common.model.MessageType;
import com.tyc.common.model.SerializeType;
import java.util.Objects;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-08 09:29:33
 */
public class SerializeStrategyContext {
    private SerializeStrategy serializeStrategy;

    public SerializeStrategyContext(Byte serializeType) {
        if(serializeType.equals(SerializeType.JSON.getCode())){
            serializeStrategy = new JSONSerialize();
        }
    }



    public Message deSerialize(byte[] data, MessageType messageType){
        Class aClass = MessageType.getClass(messageType);
        if(Objects.isNull(aClass) || Objects.isNull(serializeStrategy)){
            return null;
        }
        return serializeStrategy.deserialize(data,aClass);
    }

    public byte[] serialize(Message message){
        if(Objects.isNull(serializeStrategy)){
            return null;
        }
        return serializeStrategy.serialize(message);
    }
}
