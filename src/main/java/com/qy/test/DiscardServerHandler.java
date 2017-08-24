package com.qy.test;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * chanelRead（）事件处理方法。每当从客户端收到新的数据时，这个方法会在收到消息时被调用，这个例子中，收到的消息的类型是ByteBuf
 *
 * exceptionCaught（）事件处理方法是当出现Throwable的对象才会被调用，即当的Netty由于IO错误或者处理器在处理事件时抛出的异常时
 */
public class DiscardServerHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 出现异常关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    // 丢弃服务
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 丢弃接受到的数据
            ((ByteBuf) msg).release();
        } finally {
            // 释放msg 因为其是ByteBuf类型
            ReferenceCountUtil.release(msg);
        }
    }*/
    // 观察接收到的数据
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) { // (1)
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }*/

    // ECHO服务（响应式协议）
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
        ctx.flush();
        // 与前两行功能一样
        ctx.writeAndFlush(msg);
    }*/

    // TIME服务（时间协议的服务）

}
