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

//这个类相当于Reactor模式下的Boss
public class Server4Multiple {
    static final int port = 8001;
    static ExecutorService workerPool = Executors.newCachedThreadPool();
    //一组处理数据通道I/O的线程，每个线程包含一个选择器
    private static List<Server4Worker> workers = new ArrayList<>();
    // 绑定服务端口的ServerChannel只能有一个，因此这个ServerChannel被所有的Boss Selector共享
    static ServerSocketChannel ssChannel;
    static ExecutorService bossPool = Executors.newCachedThreadPool();
    public static void main(String[] args) throws Exception{
        //相当于启用50个选择器，来管理数据通道
        for (int i=0;i<50;i++){
            Server4Worker t = new Server4Worker();
            workers.add(t);
            workerPool.execute(t);
        }
        //启用一组线程(5个)来负责处理连接请求
        for (int i=0;i<5;i++) {
            bossPool.execute(
                    ()->{
                        try {
                            Server4Multiple server = new Server4Multiple();
                            server.service();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
            );
        }
        bossPool.shutdown();
    }
    static {
        try {
            //唯一的ServerSocketChannel初始化，绑定IP和端口
            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.bind(new InetSocketAddress("127.0.0.1", port));
            System.out.println("服务端开启服务端口" + port + "...");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Selector selector;
    //一个绑定了IP和端口的ServerSocketChannel向多个Selector注册；
    //而每个Selector由一个线程负责轮询
    public Server4Multiple() throws Exception{
        selector = Selector.open();
        // 唯一的ServerSocketChannel向每个Boss的Selector注册
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    //此处无需synchronized
    private static SocketChannel serverAccept(ServerSocketChannel sChannel){
        try {
            SocketChannel channel = sChannel.accept();
            return channel;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
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
                        SocketChannel channel = serverAccept(ssChannel);
                        if (channel!=null) {
                            System.out.println(Thread.currentThread().getName() + " 接受客户端的连接，开始会话...");
                            // 随机选择一个Worker线程去处理channel的通信
                            workers.get(new Random().nextInt(workers.size())).addChannel(channel);
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}