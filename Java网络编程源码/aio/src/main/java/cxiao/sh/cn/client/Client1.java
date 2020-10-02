package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.nio.channels.AsynchronousSocketChannel;
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

        //模拟10个客户端并发操作
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
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步
        String str = Receiver.recvString(channel);
        System.out.println("成功接收：" + str);

        str = "I am fine. Thank you!";
        Sender.sendString(channel, str);
        System.out.println("成功发送：" + str);

        str = "And you?";
        Sender.sendString(channel, str);
        System.out.println("成功发送：" + str);

        str =  Receiver.recvString(channel);
        System.out.println("成功接收：" + str);
    }
}