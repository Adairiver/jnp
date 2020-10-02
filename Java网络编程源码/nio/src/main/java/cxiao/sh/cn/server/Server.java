package cxiao.sh.cn.server;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Server {
    private Selector selector;
    protected Server(int port) throws Exception{
        selector = Selector.open();
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ssChannel.bind(new InetSocketAddress("127.0.0.1", port));
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器开启服务端口" + port + ",等待连接请求...");
    }
    protected void closeChannel(SelectionKey key) throws Exception{
        key.channel().close();
        key.cancel();
        System.out.println("会话结束，服务端关闭连接。");
    }
    public void service() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeysSet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeysSet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel = ssChannel.accept();
                        System.out.println("接受客户端的连接，开始会话...");
                        channel.configureBlocking(false);
                        registerOnConnected(channel, key.selector());
                    }else{
                        session(key);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    abstract protected void session(SelectionKey key) throws Exception;
    abstract protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception;
}