package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client2 extends Client1{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();
        int concurrentCount = 10;
        //用多线程模拟多客户端并发通信
        for (int i=0;i<concurrentCount;i++) {
            fixPool.execute(
                    ()->{
                        Client2 client = new Client2();
                        client.communicate();
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client2() {
        super("127.0.0.1",8001);
    }

    @Override
    protected void session(Socket socket) throws Exception {
        String msg = "How are you!";
        System.out.println("Send: " + msg);
        Sender.sendString(socket, msg);
        msg = Receiver.recvString(socket);
        System.out.println("Receive: " + msg);
        msg = Receiver.recvString(socket);
        System.out.println("Receive: " + msg);
        msg = "I'm fine too.";
        Sender.sendString(socket, msg);
        System.out.println("Send: " + msg);
    }
}