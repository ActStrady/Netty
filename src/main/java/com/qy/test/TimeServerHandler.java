package com.qy.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/**
 * 不接受任何请求时他会发送一个含32位的整数的消息
 * 构建和发送一个消息，然后在完成时主动关闭连接。
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    // 忽略任何接收到的数据，而只是在连接被创建发送一个消息，不在使用hannelRead()方法
    // 覆盖channelActive()方法
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // 代表当前时间的32位整数消息的构建工作
        final ByteBuf time = ctx.alloc().buffer(4);

        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}