package com.konsung.netty;

import android.util.Log;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-01-13 10:08
 * 目前康尚程序尚未使用到此客户端类
 */
public class EchoClient {
    private final String host;      // 服务器地址
    private final int port;         // 服务器端口号

    public EchoClient(String host, int port) {
        System.out.println("-------echoClient-----");
        this.host = host;
        this.port = port;
    }

    // 开始方法
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端引导器
            Bootstrap bootstrap = new Bootstrap();
            // 指定事件组、客户端通道、远程服务端地址、业务处理器
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel
                                                           socketChannel)
                                throws Exception {
                            socketChannel.pipeline().addLast(new
                                    EchoClientHandler());
                        }
                    });
            // 连接到服务端，sync()阻塞直到连接过程结束
            ChannelFuture future = bootstrap.connect().sync();
            // 等待通道关闭
            future.channel().closeFuture().sync();
        } finally {
            Log.d("Test", "EchoClient:finally");

            // 关闭引导器并释放资源，包括线程池
            group.shutdownGracefully().sync();
        }
    }
}
