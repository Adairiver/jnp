package cxiao.sh.cn.client;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Client {
    private String serverIP;
    private int port;

    protected Client(String serverIP,int port){
        this.serverIP = serverIP;
        this.port = port;
    }

    public void communicate() throws Exception{
        CountDownLatch end = new CountDownLatch(1);
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        //connect(.)交给线程池执行
        socketChannel.connect(new InetSocketAddress(serverIP, port), null,
                new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void att) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " 客户端连接成功，开始会话...");

                            session(socketChannel);

                            socketChannel.close();
                            System.out.println("会话结束，客户端关闭连接！");
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            //放在这里是为了避免在发生异常时出现死锁
                            end.countDown();
                        }

                    }

                    @Override
                    public void failed(Throwable exc, Void att) {
                        exc.printStackTrace();
                        //放在这里是为了避免在发生异常比如拒绝连接时出现死锁
                        end.countDown();
                    }
                });

        //等待，直至连接关闭，然后结束程序
        end.await();
    }

    protected abstract void session(AsynchronousSocketChannel channel) throws Exception;

}