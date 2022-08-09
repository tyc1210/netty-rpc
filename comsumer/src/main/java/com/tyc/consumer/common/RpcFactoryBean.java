package com.tyc.consumer.common;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.IDUtil;
import com.tyc.common.exception.RpcException;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.consumer.client.RpcClientManager;
import com.tyc.consumer.handler.RpcResultHandler;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.springframework.beans.factory.FactoryBean;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 生成代理对象
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 13:46:22
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {
    private Class<T> aClass;

    public RpcFactoryBean(Class<T> aClass) {
        this.aClass = aClass;
    }

    @Override
    public T getObject() throws Exception {
        // 返回目标对象的代理对象
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 调用 netty 客户端发送消息
                String classMethodName = new StringBuilder(method.getDeclaringClass().getName()).append(".").append(method.getName()).toString();
                RpcRequest rpcRequest = new RpcRequest(IDUtil.getRequestId(),classMethodName,args);
                Channel channel = RpcClientManager.channel;
                if (channel.isActive()) {
                    channel.writeAndFlush(rpcRequest);
                    // 准备Promise获取结果 传入的eventLoop代表若异步获取Promise结果由哪个线程处理
                    DefaultPromise<RpcResult> promise = new DefaultPromise<>(channel.eventLoop());
                    RpcResultHandler.map.put(rpcRequest.getId(),promise);
                    // 阻塞等待获取结果
                    promise.await();
                    if(promise.isSuccess()){
                        RpcResult rpcResult = promise.getNow();
                        return JSONObject.parseObject(rpcResult.getResultData(),method.getReturnType());
                    }else {
                        throw new RpcException(promise.cause().getMessage());
                    }
                }else {
                    throw new RpcException("连接异常");
                }
            }
        };
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{aClass},handler);
    }

    @Override
    public Class<?> getObjectType() {
        return aClass;
    }

}
