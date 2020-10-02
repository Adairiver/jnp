package cxiao.sh.cn.client;

import cxiao.sh.cn.common.protocol.AttachHeaderHandler;
import cxiao.sh.cn.common.serializable.IClientSerializer;
import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class NettyNetClient implements NetClient<Request, Response> {
    @Override
    public Response sendRequest(Request request, String sAddress, IClientSerializer<Request, Response> serializing) throws Throwable {
        String[] addInfoArray = sAddress.split(":");
        SendHandler<Request, Response> sendHandler = new SendHandler<>(request, serializing);
        Response response = null;
        // 配置客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(addInfoArray[0], Integer.valueOf(addInfoArray[1])))
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
                            ch.pipeline().addLast(sendHandler);
                            ch.pipeline().addLast(new AttachHeaderHandler());
                        }
                    });
            // 启动客户端连接
            b.connect().sync();
            response = sendHandler.getResponse();
        } finally {
            // 释放线程组资源
            group.shutdownGracefully();
        }
        return response;
    }
    //T是最初发送出去的类型，U是最后接收到的类型
    private class SendHandler<T, U> extends ChannelInboundHandlerAdapter {
        private T request;
        private U response = null;
        private IClientSerializer<T, U> serializing;
        private CountDownLatch cdl;
        public SendHandler(T request, IClientSerializer<T, U> serializing) {
            cdl = new CountDownLatch(1);
            this.request = request;
            this.serializing = serializing;
        }
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            byte[] data = this.serializing.marshalling(this.request);
            ByteBuf reqBuf = Unpooled.buffer(data.length);
            reqBuf.writeBytes(data);
            ctx.channel().write(reqBuf);
        }
        public U getResponse() {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.response;
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf msgBuf = (ByteBuf) msg;
            byte[] bytes = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(bytes);
            this.response = this.serializing.unmarshalling(bytes);
            cdl.countDown();
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}