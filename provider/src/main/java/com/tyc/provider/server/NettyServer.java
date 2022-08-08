package com.tyc.provider.server;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyc.provider.codec.DefaultLengthFieldBasedFrameDecoder;
import com.tyc.provider.codec.MessageCodec;
import com.tyc.provider.config.NacosConfigProperties;
import com.tyc.provider.handler.QuitHandler;
import com.tyc.provider.handler.RpcRequestHandler;
import com.tyc.provider.nacos.NacosTemplate;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        // 日志
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        // 自定义编解码器
        MessageCodec messageCodec = new MessageCodec();
        // 处理消息类型为RpcRequest的handler
        RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();
        try {
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG,128)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    // 处理粘包半包
                    ch.pipeline().addLast(new DefaultLengthFieldBasedFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);
                    // 心跳检测 判断读/写 时间过长
                    // 多少秒未收到数据
                    // 多少秒未进行写数据
                    // 读写都没有的空闲时间
                    // 触发对应的时间
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent)evt;
                            if(event.state() == IdleState.READER_IDLE){
                                log.info("触发读空闲事件，释放资源");
                                // 释放资源
                                ctx.channel().close();
                            }
                        }
                    });
                    // 处理连接断开
                    ch.pipeline().addLast(new QuitHandler());
                    ch.pipeline().addLast(rpcRequestHandler);
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
