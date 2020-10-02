package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.AttachHeaderHandler;
import cxiao.sh.cn.comm.BufToStringHandler;
import cxiao.sh.cn.comm.StringToBufHandler;
import cxiao.sh.cn.handlers1.ClientBusinessHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client1 extends Client{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            fixPool.execute(
                    () -> {
                        try {
                            Client1 client = new Client1();
                            client.communicate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client1(){
        super("127.0.0.1", 8001);
    }
    @Override
    void addHandlers(SocketChannel ch) {
        // 客户端操作步骤：
        // 发送 How are you?
        // 接收 I'am fine, thank you.
        // 接收 And you?
        // 发送 I'm fine too.
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
        ch.pipeline().addLast(new BufToStringHandler());
        ch.pipeline().addLast(new ClientBusinessHandler());

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new StringToBufHandler());
    }
}