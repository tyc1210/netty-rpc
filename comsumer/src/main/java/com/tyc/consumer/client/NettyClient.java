package com.tyc.consumer.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyc.common.model.PingMessage;
import com.tyc.common.model.RequestFuture;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.consumer.codec.DefaultLengthFieldBasedFrameDecoder;
import com.tyc.consumer.codec.MessageCodec;
import com.tyc.consumer.handler.QuitHandler;
import com.tyc.consumer.handler.RpcResultHandler;
import com.tyc.consumer.nacos.NacosTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:11:24
 */
@Component("nettyClient")
@Slf4j
public class NettyClient {
    @Value("${client.host}")
    private String host;
    @Value("${client.port}")
    private Integer port;
    @Value("${client.threadNum}")
    private Integer threadNum;
    @Value("${client.timeOut}")
    private Long timeOut;
    private Bootstrap bootstrap;
    private static ChannelFuture channelFuture;

    @Value("${client.serviceName}")
    private String serviceName;

    @Autowired
    private NacosTemplate nacosTemplate;


    public void start(){
        try {
            getInstance();
            init();
            log.info("客户端连接目标服务===>{}:{}",host,port);
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从配置中心获取实例
     */
    void getInstance(){
        Instance instance = nacosTemplate.getOneHealthyInstance(serviceName);
        if(StringUtils.isNotBlank(instance.getIp()) && instance.getPort() != 0){
            host = instance.getIp();
            port = instance.getPort();
        }
    }

    private void init(){
        RequestFuture.timeOut = timeOut;
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(threadNum);
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        PingMessage pingMessage = new PingMessage();
        /**
         * 添加各种 handler
         */
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec messageCodec = new MessageCodec();
        RpcResultHandler rpcResultHandler = new RpcResultHandler();
        QuitHandler quitHandler = new QuitHandler();

        bootstrap .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch)
                    throws Exception {
                ch.pipeline().addLast(new DefaultLengthFieldBasedFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageCodec);
                ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                ch.pipeline().addLast(new ChannelDuplexHandler(){
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        IdleStateEvent event = (IdleStateEvent)evt;
                        if(event.state() == IdleState.WRITER_IDLE){
                            log.debug("触发写空闲事件，发送心跳消息");
                            ctx.writeAndFlush(pingMessage);
                        }
                    }
                });
                ch.pipeline().addLast(quitHandler);
                ch.pipeline().addLast(rpcResultHandler);
            }
        });
    }

    // todo 创建客户端连接池 提高吞吐量
    public static RpcResult sendRequest(RpcRequest request){
        channelFuture.channel().writeAndFlush(request);
        return request.getRequestFuture().get();
    }

}
