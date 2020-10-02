package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.net.Socket;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server4 extends Server1{
    public static void main(String[] args) {
        Server4 server = new Server4();
        server.service();
    }
    public Server4(){
        super(8001);
    }
    @Override
    protected void session(Socket socket) throws Exception {
        String fileName = Receiver.recvString(socket);
        System.out.println("接收文件名: " + fileName);
        String filePath = "D:\\ServerFiles\\FromClient-" + fileName;
        Receiver.recvFile(socket, filePath);
        System.out.println("接收并保存文件: " + filePath);
        filePath = "D:\\ServerFiles\\xyz.mp4";
        fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
        Sender.sendString(socket, fileName);
        System.out.println("发送文件名: " + fileName);
        Sender.sendFile(socket,filePath);
        System.out.println("发送文件: " + filePath);
    }
}