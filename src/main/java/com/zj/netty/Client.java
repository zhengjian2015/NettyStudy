package com.zj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

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
            //不加下面这句客户端就直接结束了 起到阻塞的作用
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的关闭
            group.shutdownGracefully();
        }

    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientHandler());
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel第一次连上可用,写出一个字符串
        //netty 都是用ByteBuf来处理数据 它的效率高 因为是直接访问内存
        //同时跳过jvm,也跳过了垃圾回收机制,需要自己回收
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buf);
    }

    //客户端再接收服务端信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));
        } finally {
            //释放 避免内存泄露
            if(buf != null) ReferenceCountUtil.release(buf);
            System.out.println(buf.refCnt());
        }
    }
}
