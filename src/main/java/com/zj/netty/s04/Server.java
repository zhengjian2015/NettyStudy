package com.zj.netty.s04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;

public class Server {

    //实现转发,能让多个客户端保存
    //通道组
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
                        //ch指的是通道初始化完成
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //System.out.println(ch);
                            //开始处理数据 pipeline是个管道
                            ChannelPipeline pl = ch.pipeline();
                            pl.addLast(new ServerChildHandler());
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


/**
 * 管家是迎大门的
 * 服务员是读数据的
 * 通道完成后 由 服务员来读数据
 *
 */
class ServerChildHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());

    }

    //没有泛型 SimpleChannelInboundHandler由泛型
    //服务端接收客户端信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            //ridex 读指针 widx写指针 里面已经有hello了 所以要从第5个指针开始读
            //System.out.println(buf);
            //查看buf的引用
            //System.out.println(buf.refCnt());
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));
            //往客户端写
            //ctx.writeAndFlush(msg);
            Server.clients.writeAndFlush(msg);
        } finally {
            //释放 避免内存泄露 往回写时 writeflush会自动释放
            //if(buf != null) ReferenceCountUtil.release(buf);
            //System.out.println(buf.refCnt());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //记录异常
            cause.printStackTrace();
            //客户端的 f.channel().closeFuture().sync(); 会执行
            ctx.close();
    }
}
