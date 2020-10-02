package cxiao.sh.cn.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server4Single {
    static final int port = 8001;
    static ExecutorService fixPool = Executors.newCachedThreadPool();
    public static void main(String[] args) throws Exception{
        Server4Single server = new Server4Single();
        server.service();
    }
    private Selector selector;
    //一组处理数据通道I/O的线程，每个线程包含一个选择器
    private List<Server4Worker> workers = new ArrayList<>();
    public Server4Single() throws Exception{
        //相当于启用10个选择器，来管理数据通道
        for (int i=0;i<10;i++){
            Server4Worker t = new Server4Worker();
            workers.add(t);
            fixPool.execute(t);
        }
        selector = Selector.open();
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ssChannel.bind(new InetSocketAddress("127.0.0.1", port));
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端开启服务端口" + port + "...");
    }
    public void service() {
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeysSet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeysSet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel =ssChannel.accept();
                        System.out.println(Thread.currentThread().getName() + " 接受客户端的连接，开始会话...");
                        // 随机选择一个Worker让它去处理hannel的通信
                        workers.get(new Random().nextInt(workers.size())).addChannel(channel);
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}