package cxiao.sh.cn.client;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client0 {
    private String serverIP;
    private int port;
    public static void main(String[] args) {
        Client0 client = new Client0("127.0.0.1", 8001);
        client.communicate();
    }
    protected Client0(String ip, int port){
        this.serverIP = ip;
        this.port = port;
    }
    public void communicate() {
        InetSocketAddress isAddr = new InetSocketAddress(serverIP, port);
        Socket socket = new Socket();
        try {
            socket.connect(isAddr);
            System.out.println("客户端连接成功，开始会话...");
            session(socket);
            socket.close();
            System.out.println("会话结束，客户端关闭连接。");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void session(Socket socket) throws Exception{
        System.out.println("与服务端进行会话，接收或者发送数据...");
    }
}