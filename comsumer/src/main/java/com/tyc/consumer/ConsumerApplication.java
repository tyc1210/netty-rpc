package com.tyc.consumer;

import com.tyc.consumer.listener.NettyClientListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 18:15:10
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerApplication.class);
        application.addListeners(new NettyClientListener());
        application.run(args);
    }
}
