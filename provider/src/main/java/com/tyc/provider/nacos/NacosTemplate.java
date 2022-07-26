package com.tyc.provider.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyc.provider.config.NacosConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-26 16:06:59
 */
@Component
public class NacosTemplate implements InitializingBean {
    @Autowired
    private NacosConfigProperties nacosConfigProperties;
    // 配置管理
    private ConfigService configService;
    // 服务管理
    private NamingService namingService;


    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,nacosConfigProperties.getAddr());
        properties.setProperty(PropertyKeyConst.USERNAME,nacosConfigProperties.getUsername());
        properties.setProperty(PropertyKeyConst.PASSWORD,nacosConfigProperties.getPassword());
        try {
            configService = NacosFactory.createConfigService(properties);
            namingService = NacosFactory.createNamingService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册服务
     */
    public void registerServer(Instance instance) throws Exception{
        namingService.registerInstance(instance.getServiceName(),instance);
    }

    /**
     * 删除服务
     */
    public void deleteServer(Instance instance) throws Exception{
        namingService.deregisterInstance(instance.getServiceName(),instance.getIp(),instance.getPort());
    }

    /**
     * 随机全部（有可能获取到的不健康）。
     * 可以按照自己的负载均衡算法进行调用。
     */
    public List<Instance> getAllServer(String serverName) throws Exception{
        return namingService.getAllInstances(serverName);
    }

    /**
     * 根据负载均衡算法获取一个健康的实例
     */
    public Instance getOneHealthyInstance(String serverName) throws Exception{
        return namingService.selectOneHealthyInstance(serverName);
    }


}
