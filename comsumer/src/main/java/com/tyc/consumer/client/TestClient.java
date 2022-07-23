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

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.start();
        Object[] arg = new Object[]{1L};
        RpcRequest rpcRequest = new RpcRequest("com.tyc.common.service.UserService.getUserById",arg);
        channelFuture.channel().writeAndFlush(JSONObject.toJSONString(rpcRequest));
    }
}
