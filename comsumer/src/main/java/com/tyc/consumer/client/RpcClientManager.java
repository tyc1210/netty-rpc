package com.tyc.consumer.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyc.common.exception.RpcException;
import com.tyc.consumer.codec.DefaultLengthFieldBasedFrameDecoder;
import com.tyc.consumer.codec.MessageCodec;
import com.tyc.consumer.handler.HeartBeatHandler;
import com.tyc.consumer.handler.QuitHandler;
import com.tyc.consumer.handler.RpcResultHandler;
import com.tyc.consumer.nacos.NacosTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RPC 远程连接管理
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:11:24
 */
@Component("rpcClientManager")
@Slf4j
public class RpcClientManager {
    @Value("${client.host}")
    private String host;

    @Value("${client.port}")
    private Integer port;

    @Value("${client.threadNum}")
    private Integer threadNum;

    @Value("${client.serviceName}")
    private String serviceName;

    @Autowired
    private NacosTemplate nacosTemplate;

    public static volatile Channel channel;

    private final Object LOCK = new Object();


    public Channel getChannel(){
        if(null != channel && channel.isActive()){
            return channel;
        }
        synchronized (LOCK){
            if(channel != null && channel.isActive()){
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    /**
     * 从配置中心服务端地址
     */
    private void initServerAddress(){
        Instance instance = nacosTemplate.getOneHealthyInstance(serviceName);
        if(StringUtils.isNotBlank(instance.getIp()) && instance.getPort() != 0){
            host = instance.getIp();
            port = instance.getPort();
        }
    }

    private void initChannel(){
        initServerAddress();
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(threadNum);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        /**
         * 添加各种 handler
         */
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec messageCodec = new MessageCodec();
        RpcResultHandler rpcResultHandler = new RpcResultHandler();

        try {
            bootstrap .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new DefaultLengthFieldBasedFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    ch.pipeline().addLast(new IdleStateHandler(10,0,0));
                    ch.pipeline().addLast(new HeartBeatHandler());
                    ch.pipeline().addLast(new QuitHandler());
                    ch.pipeline().addLast(rpcResultHandler);
                }
            });
            channel = bootstrap.connect(host, port).sync().channel();
            channel.closeFuture().addListener(future -> {
                loopGroup.shutdownGracefully();
            });
        } catch (Throwable throwable) {
            throw new RpcException(throwable.getMessage());
        }
    }

}
