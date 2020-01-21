package com.zj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

        //netty默认都是异步的，加上sync表示等待返回 channel指定通道类型nio 非阻塞版,SockentChannel是bio,
        //handler指交给哪个事件去处理
        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost",8888);

            //用监听来判断是因为 f的结果是异步的，没有等它返回就往下执行了
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        System.out.println("not connected");
                    } else {
                        System.out.println("connected");
                    }
                }
            });

            f.sync();
            System.out.println("...");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println(ch);
    }
}
