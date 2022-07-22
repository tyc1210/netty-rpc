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
public class RpcRequest {
    private Long id;
    private String methodName;
    private Object[] args;
    private RequestFuture requestFuture;
    private static AtomicLong atomicInteger = new AtomicLong(0);

    public RpcRequest(Long id, String methodName, Object[] args) {
        this.id = id;
        this.methodName = methodName;
        this.args = args;
        this.requestFuture = new RequestFuture();
        RequestFuture.rpcRequestMap.put(id,this);
    }

    public RpcRequest(String methodName, Object[] args) {
        this.id = atomicInteger.incrementAndGet();
        this.methodName = methodName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
