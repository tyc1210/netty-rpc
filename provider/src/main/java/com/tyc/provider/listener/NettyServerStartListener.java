package com.tyc.provider.listener;

import com.tyc.provider.annotation.RpcService;
import com.tyc.provider.cache.MethodCache;
import com.tyc.provider.server.NettyServer;
import com.tyc.provider.util.ScanUtill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

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
        log.info("扫描服务者提供信息放入缓存，提供根据方法信息调用");
        // 将特定注解的bean 生成代理对象放入缓存 反射调用
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Set<Class<?>> classes = ScanUtill.scanAnnotation("com.tyc.provider", RpcService.class);
        for (Class<?> aClass : classes) {
            String[] beanNames = applicationContext.getBeanNamesForType(aClass);
            for (String beanName : beanNames) {
                log.info("处理beanName:{},生成代理对象放入缓存",beanName);
                Class<?> classBean = applicationContext.getType(beanName);
                Method[] methods = classBean.getMethods();
                for (Method method : methods) {
                    try {
                        // 返回声明此Method的Class对象
                        if(method.getDeclaringClass() == aClass){
                            log.info("获取目标方法名：{}",method.getName());
                            log.info("测试执行结果",method.invoke(applicationContext.getBean(beanName,aClass),1L));
                            MethodCache.MethodContext methodContext = new MethodCache.MethodContext(method, beanName);
                            String key = new StringBuilder(aClass.getInterfaces()[0].getName()).append(".").append(method.getName()).toString();
                            MethodCache.methodMap.put(key,methodContext);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
