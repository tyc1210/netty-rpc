package com.tyc.trpc;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.CountDownLatch;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-18 16:33:06
 */
@Configuration
@EnableConfigurationProperties(TrpcProperties.class)
public class TrpcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.trpc", name = "server", havingValue = "true") // 当配置trpc.server=true执行
    public TrpcServer trpcServer() {
        final  TrpcServer trpcServer = new TrpcServer();
        final CountDownLatch latch = new CountDownLatch(1);
        Thread awaitThread = new Thread(()->{
            latch.countDown();
//            trpcServer.await();
        },"trpcServer");
        awaitThread.setContextClassLoader(this.getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return trpcServer;
    }
}
