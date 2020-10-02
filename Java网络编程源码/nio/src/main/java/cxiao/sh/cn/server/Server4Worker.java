package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server4Worker implements Runnable {
    // 一个Selector管理多个Channel。
    private Selector selector;
    //用于暂存将要加入selector的数据通道
    private List<SocketChannel> bufChannels = new ArrayList<>();
    public Server4Worker() throws Exception{
        selector = Selector.open();
    }
    synchronized public void addChannel(SocketChannel channel){
        bufChannels.add(channel);
    }
    synchronized private void registerAllBufferedChannels() throws Exception{
        Iterator<SocketChannel> iterator = bufChannels.iterator();
        while (iterator.hasNext()) {
            SocketChannel channel = iterator.next();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ, new Receiver(0,Object.class));
            System.out.println(Thread.currentThread().getName() + " 数据通道注册成功！");
            iterator.remove();
        }
    }
    private void closeChannel(SelectionKey key) throws Exception{
        key.channel().close();
        key.cancel();
        System.out.println("会话结束，服务端关闭连接。");
    }
    @Override
    public void run() {
        while(true) {
            try {
                registerAllBufferedChannels();
                //这个超时设置很重要！！！
                //因为.select()是阻塞的，阻塞时新注册的channel对selector而言是检测不到的
                selector.select(500);
                Set<SelectionKey> selectedKeysSet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeysSet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        Receiver receiver = (Receiver)key.attachment();
                        if (receiver.recvFully(channel)){
                            Object obj = receiver.getObject();
                            System.out.println(Thread.currentThread().getName() + " 接收对象: " + obj);
                            if (receiver.getStepIndex()==0) {
                                channel.register(selector, SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex()+1, StuRepository.getStudent()));
                            }
                            if (receiver.getStepIndex()==3){
                                closeChannel(key);
                            }
                        }
                    } else{
                        Sender sender = (Sender)key.attachment();
                        if (sender.sendFully(channel)){
                            System.out.println(Thread.currentThread().getName() + " 发送对象: " + sender.getObject());
                            //根据步骤号设定Sender的信息
                            if (sender.getStepIndex()==1) {
                                //因为允许的操作相同，所以不用重复注册，只需修改attachment对象
                                key.attach(new Sender(sender.getStepIndex()+1, StuRepository.getStudent()));
                            }
                            if (sender.getStepIndex()==2) {
                                channel.register(selector, SelectionKey.OP_READ, new Receiver(sender.getStepIndex()+1, Object.class));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}