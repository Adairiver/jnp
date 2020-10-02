package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.AttachHeaderHandler;
import cxiao.sh.cn.comm.SegFileHandler;
import cxiao.sh.cn.handlers3.ClientBusinessHandler;
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

public class Client3 extends Client{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 1; i++) {
            fixPool.execute(
                    () -> {
                        try {
                            Client3 client = new Client3();
                            client.communicate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();

    }
    public Client3(){
        super("127.0.0.1", 8001);
    }

    @Override
    void addHandlers(SocketChannel ch) {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4,0,4));
        ch.pipeline().addLast(new ClientBusinessHandler("D:\\ClientFiles\\"));

        ch.pipeline().addLast(new AttachHeaderHandler());
        ch.pipeline().addLast(new SegFileHandler());
    }
}