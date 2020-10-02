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
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步
        String filePath = "D:\\ClientFiles\\abc.mp4";
        String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
        Sender.sendString(channel, fileName);
        System.out.println("发送文件名: " + fileName);
        Sender.sendFile(channel,filePath);
        System.out.println("发送文件: " + filePath);

        fileName = Receiver.recvString(channel);
        System.out.println("接收文件名: " + fileName);
        filePath = "D:\\ClientFiles\\FromServer-" + fileName;
        Receiver.recvFile(channel, filePath);
        System.out.println("接收并保存文件: " + filePath);

        for(int i=0;i<10;i++) {
            fileName = Receiver.recvString(channel);
            System.out.println("接收文件名: " + fileName);
            filePath = "D:\\ClientFiles\\" + i + fileName;
            Receiver.recvFile(channel, filePath);
            System.out.println("接收并保存文件: " + filePath);
        }

        for(int i=0;i<10;i++){
            filePath = "D:\\ClientFiles\\abc.txt";
            fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
            Sender.sendString(channel, fileName);
            System.out.println("发送文件名: " + fileName);
            Sender.sendFile(channel,filePath);
            System.out.println("发送文件: " + filePath);
        }
    }
}