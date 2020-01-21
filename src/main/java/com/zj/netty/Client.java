package com.zj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    public static void main(String[] args) {
        //线程池
        EventLoopGroup group = new NioEventLoopGroup();

        //辅助类
        Bootstrap b = new Bootstrap();

        //netty默认都是异步的，加上sync表示等待返回
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                        }
                    })
                    .connect("localhost",8888)
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
