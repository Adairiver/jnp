package cxiao.sh.cn.client;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Client1 {
    private String serverIP;
    private int port;
    protected Client1(String ip, int port){
        this.serverIP = ip;
        this.port = port;
    }
    public void communicate() {
        InetSocketAddress isAddr = new InetSocketAddress(serverIP, port);
        Socket socket = new Socket();
        try {
            //设置客户端的连接超时为30000毫秒
            socket.connect(isAddr , 30000 );
            System.out.println("客户端连接成功，开始会话...");
            //设置客户端的读超时为20000毫秒
            socket.setSoTimeout(20000);
            session(socket);
            socket.close();
            System.out.println("会话结束，客户端关闭连接。");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    abstract protected void session(Socket socket) throws Exception;
}