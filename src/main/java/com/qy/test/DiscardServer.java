package com.qy.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * (1):NioEventLoopGroup是用来处理I / O操作的多线程事件循环器，的Netty提供了许多不同的EventLoopGroup的实现用来处理不同传输协议。
 * 在这个例子中我们实现了一个服务端的应用，因此会有2个NioEventLoopGroup会被使用。第一个经常被叫做'老板'，用来接收进来的连接。第二个经常被叫做
 * '工人'，用来处理已经被接收的连接，一旦'老板'接收到连接，就会把连接信息注册到'worker'上。如何知道多少个线程已经被使用，如何映射到已经创建的
 * Channels上都需要依赖于EventLoopGroup的实现，并且可以通过构造函数来配置他们的关系。
 *
 * (2):ServerBootstrap是一个启动NIO服务的辅助启动类。你可以在这个服务中直接使用频道，但是这会是一个复杂的处理过程，在很多情况下你并不需要这样做。
 *
 * (3):这里我们指定使用NioServerSocketChannel类来举例说明一个新的渠道如何接收进来的连接。
 *
 * (4):这里的事件处理类经常会被用来处理一个最近的已经接收的信道。ChannelInitializer是一个特殊的处理类，他的目的是帮助使用者配置一个新的频道。
 * 也许你想通过增加一些处理类比如DiscardServerHandle来配置一个新的频道或者其对应的的ChannelPipeline来实现你的网络程序。当你的程序变的复杂时，
 * 可能你会增加更多的处理类到pipline上，然后提取这些匿名类到最顶层的类上。
 *
 * (5):你可以设置这里指定的通道实现的配置参数。我们正在写一个TCP / IP的服务端，因此我们被允许设置插座的参数选项比如tcpNoDelay和的keepAlive。
 * (6)option（）是提供给NioServerSocketChannel用来接收进来的连接.
 * childOption（）是提供给由父管道ServerChannel接收到的连接，在这个例子中也是NioServerSocketChannel。
 * (7)我们继续，剩下的就是绑定端口然后启动服务。这里我们在机器上绑定了机器所有网卡上的8080端口。当然现在你可以多次调用bind（）的方法（基于不同绑定地址）。
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                        .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // 绑定并开始接受传入的连接.
            ChannelFuture f = b.bind(port).sync(); // (7)
            // 关闭服务器
            f.channel().closeFuture().sync();
        } finally {
            // 回收相关资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new DiscardServer(port).run();
    }
}
