package com.tyc.consumer;

import com.tyc.consumer.annotation.RpcReference;
import com.tyc.consumer.util.ScanUtill;
import com.tyc.consumer.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 14:11:41
 */
@Component
public class RegistRpcBean implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // 获取需要代理的类
        String scanPath = "com.tyc.consumer";
        Set<Class<?>> classes = ScanUtill.scanAnnotationField(scanPath, RpcReference.class);
        // 开始注入
        for (Class<?> aClass : classes) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(RpcFactoryBean.class);
            beanDefinition.setScope("singleton");
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(aClass);
            BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(beanDefinition, StringUtil.captureName(aClass.getSimpleName())),beanDefinitionRegistry);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcReference.class);
        System.out.println(1);
    }
}
