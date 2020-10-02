package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.AttachHeaderHandler;
import cxiao.sh.cn.comm.BufToStringHandler;
import cxiao.sh.cn.comm.StringToBufHandler;
import cxiao.sh.cn.handlers1.ServerBusinessHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server1 extends Server{
    public static void main(String[] args) {
        Server1 server = new Server1();
        server.service();
    }
    public Server1(){
        super(8001);
    }

    @Override
    void addHandlers(SocketChannel ch) {
        // 服务端操作步骤：
        // 接收 How are you?
        // 发送 I'am fine, thank you.
        // 发送 And you?
        // 接收 I'm fine too.
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
        ch.pipeline().addLast(new BufToStringHandler());
        ch.pipeline().addLast(new ServerBusinessHandler());

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new StringToBufHandler());
    }
}