package cxiao.sh.cn.server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server0 {
    private int port;
    public static void main(String[] args) {
        Server0 server = new Server0(8001);
        server.service();
    }
    public Server0(int port){
        this.port = port;
    }
    public void service(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器开启服务端口" + port + ",等待连接请求...");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("接受客户端的连接，开始会话...");
                session(socket);
                socket.close();
                System.out.println("会话结束，服务端关闭连接。");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void session(Socket socket) throws Exception{
        System.out.println("与客户端进行会话，接收或者发送数据...");
    }
}