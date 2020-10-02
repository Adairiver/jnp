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

public class Server3 extends Server{
    public static void main(String[] args) throws Exception {
        Server3 server = new Server3();
        server.service();
    }
    public Server3() throws Exception{
        super(8001);
    }
    @Override
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步
        String fileName = Receiver.recvString(channel);
        System.out.println("接收文件名: " + fileName);
        String filePath = "D:\\ServerFiles\\FromClient-" + fileName;
        Receiver.recvFile(channel, filePath);
        System.out.println("接收并保存文件: " + filePath);
        filePath = "D:\\ServerFiles\\xyz.mp4";
        fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
        Sender.sendString(channel, fileName);
        System.out.println("发送文件名: " + fileName);
        Sender.sendFile(channel,filePath);
        System.out.println("发送文件: " + filePath);

        for(int i=0;i<10;i++){
            filePath = "D:\\ServerFiles\\xyz.pdf";
            fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
            Sender.sendString(channel, fileName);
            System.out.println("发送文件名: " + fileName);
            Sender.sendFile(channel,filePath);
            System.out.println("发送文件: " + filePath);
        }

        for(int i=0;i<10;i++){
            fileName = Receiver.recvString(channel);
            System.out.println("接收文件名: " + fileName);
            filePath = "D:\\ServerFiles\\" + i + fileName;
            Receiver.recvFile(channel, filePath);
            System.out.println("接收并保存文件: " + filePath);
        }
    }
}