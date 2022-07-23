package com.tyc.consumer.common;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.consumer.client.NettyClient;
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
                RpcRequest rpcRequest = new RpcRequest(classMethodName,args);
                RpcResult rpcResult = NettyClient.sendRequest(rpcRequest);
                return JSONObject.parseObject(rpcResult.getResultData(),method.getReturnType());
            }
        };
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{aClass},handler);
    }

    @Override
    public Class<?> getObjectType() {
        return aClass;
    }

}
