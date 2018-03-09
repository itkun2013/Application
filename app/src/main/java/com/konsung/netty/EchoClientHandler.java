package com.konsung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-01-13 11:13
 * 目前康尚程序尚未使用到此客户端类
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        // 连接建立，向服务端发送数据
        ctx.write(Unpooled.copiedBuffer("hello Netty!", CharsetUtil.UTF_8));
        // 注意：需要调用flush将数据发送到服务端
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                ByteBuf byteBuf) throws Exception {
        // 读取服务端返回的数据并打印
        System.out.println("Client received: " + ByteBufUtil.hexDump(byteBuf
                .readBytes(byteBuf.readableBytes())));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
//        super.exceptionCaught(ctx, cause);
        // 打印异常并关闭通道
        cause.printStackTrace();
        ctx.close();
    }
}
