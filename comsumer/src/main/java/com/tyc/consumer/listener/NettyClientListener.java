package com.tyc.consumer.listener;

import com.tyc.consumer.client.RpcClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

/**
 * spring容器初始化完后 初始化 client channel
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:10:05
 */
@Slf4j
public class NettyClientListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        RpcClientManager rpcClientManager = contextRefreshedEvent.getApplicationContext().getBean("rpcClientManager", RpcClientManager.class);
        rpcClientManager.getChannel();
    }
}
