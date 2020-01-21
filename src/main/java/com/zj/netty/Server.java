package com.zj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {

        //迎接线程，类似大管家
        EventLoopGroup bootGroup = new NioEventLoopGroup(1);
        //服务员 处理线程
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bootGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println(ch);
                        }
                    }).bind(8888)
                    .sync();

            System.out.println("server started!");
            //机器人  接收关闭命令 关闭线程
            f.channel().closeFuture().sync(); //close() -> channelFuture

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
