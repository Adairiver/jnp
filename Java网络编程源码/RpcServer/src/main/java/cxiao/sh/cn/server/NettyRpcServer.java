package cxiao.sh.cn.server;

import cxiao.sh.cn.common.protocol.AttachHeaderHandler;
import cxiao.sh.cn.common.serializable.IServerSerializer;
import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class NettyRpcServer extends RpcServer {
    public NettyRpcServer(int port, String protocol, RequestHandler handler, IServerSerializer<Request, Response> serializer) {
        super(port, protocol, handler, serializer);
    }
    private Channel channel;
    @Override
    public void start() {
        RecvHandler recvHandler = new RecvHandler(this.handler, this.serializer);
        // 配置服务器
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
                    ch.pipeline().addLast(recvHandler);
                    ch.pipeline().addLast(new AttachHeaderHandler());
                }
            });
            // 启动服务
            ChannelFuture f = b.bind().sync();
            System.out.println("完成服务端端口绑定与启动...");
            this.channel = f.channel();
            // 等待服务通道关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    @Override
    public void stop() {
        this.channel.close();
    }
    @ChannelHandler.Sharable
    private class RecvHandler extends ChannelInboundHandlerAdapter {
        private RequestHandler handler;
        private IServerSerializer<Request, Response> serializer;
        public RecvHandler(RequestHandler handler, IServerSerializer<Request, Response> serializer){
            this.handler = handler;
            this.serializer = serializer;
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf msgBuf = (ByteBuf) msg;
            byte[] reqBytes = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(reqBytes);
            Request request = this.serializer.unmarshalling(reqBytes);
            Response response = this.handler.handleRequest(request);
            byte[] rspBytes = this.serializer.marshalling(response);
            ByteBuf rspBuf = Unpooled.buffer(rspBytes.length);
            rspBuf.writeBytes(rspBytes);
            ctx.channel().write(rspBuf);
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}