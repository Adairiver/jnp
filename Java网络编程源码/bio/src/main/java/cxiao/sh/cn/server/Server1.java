package cxiao.sh.cn.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Server1 {
    private int port;
    protected  Server1(int port){
        this.port = port;
    }
    //线程池，每个会话由一个线程处理
    private ExecutorService fixPool = Executors.newCachedThreadPool();
    public void service(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器开启服务端口" + port + ",等待连接请求...");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("接受客户端的连接，开始会话...");
                //设置服务端socket读超时为20000毫秒
                socket.setSoTimeout(20000);
                //把会话操作提交给线程池，由另外的线程单独处理会话。
                fixPool.execute(
                        ()->{
                            try {
                                session(socket);
                                socket.close();
                                System.out.println("会话结束，服务端关闭连接。");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    abstract protected void session(Socket socket) throws Exception;
}