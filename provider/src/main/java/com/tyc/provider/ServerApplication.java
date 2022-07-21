package com.tyc.provider;

import com.tyc.provider.listener.NettyServerStartListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:29:50
 */
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ServerApplication.class);
        application.addListeners(new NettyServerStartListener());
        application.run(args);
    }
}
