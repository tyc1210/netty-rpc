package com.tyc.consumer.client;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RequestFuture;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.consumer.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
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


    public void start(){
        try {
            init();
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        RequestFuture.timeOut = timeOut;
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(threadNum);
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        final ClientHandler handler = new ClientHandler();
        bootstrap .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch)
                    throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                        0, 4, 0, 4));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(handler);
                ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                ch.pipeline().addLast(new StringEncoder());
            }
        });
    }

    public static RpcResult sendRequest(RpcRequest request){
//        RpcRequest rpcRequest = new RpcRequest();
//        rpcRequest.setId(1L);
//        rpcRequest.setMethodName("com.tyc.common.service.UserService.getUserById");
//        rpcRequest.setArgs(new Object[]{1L});
        channelFuture.channel().writeAndFlush(JSONObject.toJSONString(request));
        return request.getRequestFuture().get();
    }

}
