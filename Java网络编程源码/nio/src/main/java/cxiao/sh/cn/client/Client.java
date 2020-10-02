package cxiao.sh.cn.client;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public abstract class Client {
    private Selector selector;
    protected Client(String serverIP, int port) throws Exception{
        selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(new InetSocketAddress(serverIP, port));
    }
    abstract protected void session(SelectionKey key) throws Exception;
    abstract protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception;
    protected void closeChannel(SelectionKey key) throws Exception{
        key.channel().close();
        key.selector().close();
    }

    public void communicate() throws Exception{
        while (selector.isOpen()){
            selector.select();
            Set<SelectionKey> selectedKeysSet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeysSet.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()){
                    SocketChannel channel = (SocketChannel)key.channel();
                    while(!channel.finishConnect()){}
                    System.out.println("客户端连接成功，开始会话...");
                    registerOnConnected(channel, key.selector());
                }else {
                    session(key);
                }
            }
        }
        System.out.println("会话结束，客户端关闭连接。");
    }
}