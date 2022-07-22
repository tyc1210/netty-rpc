package com.tyc.common.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:34:33
 */
public class RequestFuture {
    public static Map<Long,RpcRequest> rpcRequestMap = new ConcurrentHashMap<Long, RpcRequest>();
    public static Long timeOut = 500L;
    private volatile RpcResult rpcResult;
    // 结果是否到来
    private CountDownLatch countDownLatch = new CountDownLatch(1);


    public RpcResult get() {
        while (rpcResult == null){
            try {
                countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rpcResult;
    }

    public void receive(RpcResult rpcResult){
        this.rpcResult = rpcResult;
        RpcRequest rpcRequest = rpcRequestMap.remove(rpcResult.getId());
        rpcRequest.getRequestFuture().countDownLatch.countDown();
    }
}
