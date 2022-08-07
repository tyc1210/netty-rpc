package com.tyc.common.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 封装请求信息
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 17:57:24
 */
public class RpcRequest extends Message{
    private Integer id;
    private String classMethodName;
    private Object[] args;
    private RequestFuture requestFuture;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public RpcRequest(Integer id, String methodName, Object[] args) {
        this.id = id;
        this.classMethodName = methodName;
        this.args = args;
        this.requestFuture = new RequestFuture();
        RequestFuture.rpcRequestMap.put(id,this);
    }

    public RpcRequest(String methodName, Object[] args) {
        this.id = atomicInteger.incrementAndGet();
        this.classMethodName = methodName;
        this.args = args;
        this.requestFuture = new RequestFuture();
        RequestFuture.rpcRequestMap.put(id,this);
    }

    public RequestFuture getRequestFuture() {
        return requestFuture;
    }

    public void setRequestFuture(RequestFuture requestFuture) {
        this.requestFuture = requestFuture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethodName() {
        return classMethodName;
    }

    public void setMethodName(String methodName) {
        this.classMethodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public SerializeType getSerializeType() {
        return SerializeType.JSON;
    }

    public MessageType getMessageType() {
        return MessageType.REQUEST;
    }

    public Integer getMessageId() {
        return this.id;
    }
}
