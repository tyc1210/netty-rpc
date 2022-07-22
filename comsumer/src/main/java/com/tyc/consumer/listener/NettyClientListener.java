package com.tyc.consumer.listener;

import com.tyc.consumer.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:10:05
 */
@Slf4j
public class NettyClientListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        NettyClient nettyClient = contextRefreshedEvent.getApplicationContext().getBean("nettyClient", NettyClient.class);
        log.info("开启客户端连接");
        nettyClient.start();
    }
}
