package com.tyc.provider.listener;

import com.tyc.provider.server.NettyServer;
import com.tyc.provider.util.ScanUtill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:31:45
 */
@Slf4j
public class NettyServerStartListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("开始启动netty服务");
        new Thread(()->{
            NettyServer nettyServer = contextRefreshedEvent.getApplicationContext().getBean("nettyServer", NettyServer.class);
            nettyServer.start();
        }).start();
        log.info("扫描服务者提供信息放入缓存");
        // 将特定注解的bean 生成代理对象放入缓存 反射调用
    }
}
