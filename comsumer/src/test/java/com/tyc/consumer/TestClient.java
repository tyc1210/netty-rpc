package com.tyc.consumer;

import com.alibaba.fastjson.JSONObject;
import com.tyc.common.model.RpcRequest;
import com.tyc.common.model.RpcResult;
import com.tyc.consumer.codec.MessageCodec;
import com.tyc.consumer.handler.RpcResultHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestClient {

    private String host = "127.0.0.1";
    private Integer port = 30001;
    private Integer threadNum = 1;
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
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(threadNum);
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch)
                    throws Exception {
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(new MessageCodec());
                ch.pipeline().addLast(new RpcResultHandler());
            }
        });
    }

    public static RpcResult sendRequest(RpcRequest request){
//        RpcRequest rpcRequest = new RpcRequest();
//        rpcRequest.setId(1L);
//        rpcRequest.setMethodName("com.tyc.common.service.UserService.getUserById");
//        rpcRequest.setArgs(new Object[]{1L});
//        channelFuture.channel().writeAndFlush(JSONObject.toJSONString(request));
//        return request.getRequestFuture().get();
        return null;
    }

    public static void main(String[] args) {
//        TestClient client = new TestClient();
//        client.start();
//        Object[] arg = new Object[]{1L};
//        RpcRequest rpcRequest = new RpcRequest("com.tyc.common.service.UserService.getUserById",arg);
////        channelFuture.channel().writeAndFlush(JSONObject.toJSONString(rpcRequest));
//        channelFuture.channel().writeAndFlush(rpcRequest);
//        System.out.println();
    }
}
