package com.tyc.consumer;

import org.springframework.beans.factory.FactoryBean;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 13:46:22
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {
    private Class<T> aClass;

    @Override
    public T getObject() throws Exception {
        // 返回目标对象的代理对象
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return aClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
