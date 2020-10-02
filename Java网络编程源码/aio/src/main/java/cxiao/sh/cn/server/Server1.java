package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server1 extends Server{
    public static void main(String[] args) throws Exception {
        Server1 server = new Server1();
        server.service();
    }

    public Server1() throws Exception{
        super(8001);
    }

    @Override
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步
        String str = "How are you?";
        Sender.sendString(channel, str);
        System.out.println("成功发送:" + str);

        str =  Receiver.recvString(channel);
        System.out.println("成功接收：" + str);

        str =  Receiver.recvString(channel);
        System.out.println("成功接收：" + str);

        str = "I'm fine too.";
        Sender.sendString(channel, str);
        System.out.println("成功发送:" + str);
    }
}