package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.*;
import cxiao.sh.cn.handlers2.ClientBusinessHandler;
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

public class Client2 extends Client{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            fixPool.execute(
                    () -> {
                        try {
                            Client2 client = new Client2();
                            client.communicate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();

    }
    public Client2(){
        super("127.0.0.1", 8001);
    }

    @Override
    void addHandlers(SocketChannel ch) {
        // 客户端操作步骤：
        // 接收 一个对象
        // 发送 一个对象
        // 发送 一个对象
        // 接收 一个对象
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4, 0, 4));
        ch.pipeline().addLast(new UnSerializer());
        ch.pipeline().addLast(new ClientBusinessHandler());

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new Serializer());
    }

}