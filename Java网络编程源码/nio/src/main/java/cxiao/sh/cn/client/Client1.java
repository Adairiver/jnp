package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client1 extends Client{
    public static void main(String[] args) throws Exception{
        ExecutorService fixPool = Executors.newCachedThreadPool();
        int concurrentCount = 3;
        for (int i=0;i<concurrentCount;i++) {
            fixPool.execute(
                    ()->{
                        try {
                            Client1 client = new Client1();
                            client.communicate();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client1() throws Exception{
        super("127.0.0.1",8001);
    }
    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception {
        channel.register(selector, SelectionKey.OP_WRITE, new Sender(0, "How are you?"));
    }
    @Override
    protected void session(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel)key.channel();
        if (key.isReadable()) {
            Receiver receiver = (Receiver)key.attachment();
            if (receiver.recvFully(channel)){
                String s = (String)receiver.getObject();
                System.out.println("Receive: " + s);
                if (receiver.getStepIndex()==1){
                    //关注的事件类型不变，故不用重复注册，只需设置attachment
                    key.attach(new Receiver(receiver.getStepIndex() + 1,String.class));
                }
                if (receiver.getStepIndex()==2) {
                    channel.register(key.selector(), SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex() + 1, "I'm fine, too."));
                }
            }
        }else {
            Sender sender = (Sender)key.attachment();
            if (sender.sendFully(channel)){
                System.out.println("Send: " + sender.getObject());
                if (sender.getStepIndex()==0) {
                    channel.register(key.selector(), SelectionKey.OP_READ, new Receiver(sender.getStepIndex() + 1, String.class));
                }
                if (sender.getStepIndex()==3) {
                    this.closeChannel(key);
                }
            }
        }
    }
}