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

public class Server2 extends Server1{
    public static void main(String[] args) {
        Server2 server = new Server2();
        server.service();
    }
    public Server2(){
        super(8001);
    }
    @Override
    protected void session(Socket socket) throws Exception {
        String msg = Receiver.recvString(socket);
        System.out.println("Receive: " + msg);
        msg = "Fine, thank you!";
        Sender.sendString(socket, msg);
        System.out.println("Send: " + msg);
        msg = "And you?";
        Sender.sendString(socket, msg);
        System.out.println("Send: " + msg);
        msg = Receiver.recvString(socket);
        System.out.println("Receive: " + msg);
    }
}