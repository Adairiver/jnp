package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client4 extends Client1{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();
        //由于在一台机器上模拟多客户端，为了避免文件访问冲突，故设为1个客户端
        for (int i=0;i<1;i++) {
            fixPool.execute(
                    ()->{
                        Client4 client = new Client4();
                        client.communicate();
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client4(){
        super("127.0.0.1",8001);
    }
    @Override
    protected void session(Socket socket) throws Exception {
        String filePath = "D:\\ClientFiles\\abc.mp4";
        String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
        Sender.sendString(socket, fileName);
        System.out.println("发送文件名: " + fileName);
        Sender.sendFile(socket,filePath);
        System.out.println("发送文件: " + filePath);
        fileName = Receiver.recvString(socket);
        System.out.println("接收文件名: " + fileName);
        filePath = "D:\\ClientFiles\\FromServer-" + fileName;
        Receiver.recvFile(socket, filePath);
        System.out.println("接收并保存文件: " + filePath);
    }
}