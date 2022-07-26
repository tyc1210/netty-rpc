package com.tyc.provider.server;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyc.provider.config.NacosConfigProperties;
import com.tyc.provider.handler.ServerHandler;
import com.tyc.provider.nacos.NacosTemplate;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:34:04
 */
@Slf4j
@Component("nettyServer")
public class NettyServer {
    @Value("${nettyServer.port}")
    private Integer port;

    @Value("${nettyServer.discoveryIp}")
    private String discoveryIp;

    @Value("${nettyServer.name}")
    private String serviceName;

    private ChannelFuture future = null;

    @Autowired
    private NacosTemplate nacosTemplate;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    public void register(){
        Instance instance = new Instance();
        instance.setServiceName(serviceName);
        instance.setIp(discoveryIp);
        instance.setPort(port);
        try {
            nacosTemplate.registerServer(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG,128)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                            0, 4, 0, 4));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new ServerHandler());
                    ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast(new StringEncoder());
                }
            });
            log.info("启动netty服务,监听端口:{}",port);
            register();
            future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
