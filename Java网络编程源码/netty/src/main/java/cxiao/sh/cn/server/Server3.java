package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.AttachHeaderHandler;
import cxiao.sh.cn.comm.SegFileHandler;
import cxiao.sh.cn.handlers3.ServerBusinessHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server3 extends Server{
    public static void main(String[] args) {
        Server3 server = new Server3();
        server.service();
    }
    public Server3(){
        super(8001);
    }

    @Override
    void addHandlers(SocketChannel ch) {
        //服务端会话操作：
        // 接收文件、发送文件、发送文件、接收文件
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4,0,4));
        ch.pipeline().addLast(new ServerBusinessHandler("D:\\ServerFiles\\"));

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new SegFileHandler());
    }
}