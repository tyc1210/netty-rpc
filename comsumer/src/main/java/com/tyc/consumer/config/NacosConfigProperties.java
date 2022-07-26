package com.tyc.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-26 16:31:31
 */
@ConfigurationProperties(prefix = "client.nacos")
@Data
@Configuration
public class NacosConfigProperties {
    private String addr;
    private String username;
    private String password;
}
