package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.*;
import cxiao.sh.cn.handlers2.ServerBusinessHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server2 extends Server{
    public static void main(String[] args) {
        Server2 server = new Server2();
        server.service();
    }
    public Server2(){
        super(8001);
    }
    @Override
    void addHandlers(SocketChannel ch) {
        // 服务端操作步骤：
        // 发送 一个对象
        // 接收 一个对象
        // 接收 一个对象
        // 发送 对象列表
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
        ch.pipeline().addLast(new UnSerializer());
        ch.pipeline().addLast(new ServerBusinessHandler());

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new Serializer());
    }
}