package cxiao.sh.cn.server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Server {
    private AsynchronousServerSocketChannel serverSocketChannel;

    protected Server(int port) throws Exception{
        serverSocketChannel =AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(port));
        System.out.println("服务器开启服务端口" + port + ",等待连接请求...");
    }
    public void service() {
        // 不需要写 while(true)
        // 因为递归代替了循环
        // 使用回调方法的异步函数，不是由主线程取得函数的返回值，
        // 而是在函数结束时的回调方法里直接使用该函数的返回值。

        //accept()交给线程池执行
        serverSocketChannel.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel channel, Void att) {
                        serverSocketChannel.accept(null, this);

                        try{
                            System.out.println(Thread.currentThread().getName() + "服务器接受连接请求，开始会话...");

                            session(channel);

                            channel.close();
                            System.out.println("会话结束，服务器关闭连接！");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void failed(Throwable exc, Void att) {
                        exc.printStackTrace();
                    }
                });

        try { //防止主线程退出
            Thread.currentThread().join();
        } catch (InterruptedException e) { }
    }

    protected abstract void session(AsynchronousSocketChannel channel) throws Exception;
}