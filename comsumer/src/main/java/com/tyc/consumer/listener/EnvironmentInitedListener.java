package com.tyc.consumer.listener;

import com.tyc.consumer.common.RegistRpcBean;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-09 17:05:14
 */
public class EnvironmentInitedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        ConfigurableEnvironment environment = applicationEnvironmentPreparedEvent.getEnvironment();
        RegistRpcBean.scanPath = environment.getProperty("client.scanPath");
    }
}
