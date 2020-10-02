package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import java.nio.channels.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server1 extends Server{
    public static void main(String[] args) throws Exception{
        Server1 server = new Server1();
        server.service();
    }
    public Server1() throws Exception{
        super(8001);
    }
    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws ClosedChannelException {
        channel.register(selector, SelectionKey.OP_READ, new Receiver(0, String.class));
    }
    @Override
    protected void session(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        if (key.isReadable()){
            Receiver receiver = (Receiver)key.attachment();
            if (receiver.recvFully(channel)){
                String s = (String)receiver.getObject();
                System.out.println("Receive: " + s);
                if (receiver.getStepIndex()==0) {
                    channel.register(key.selector(), SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex() + 1,"I'm Fine, thank u!"));
                }
                if (receiver.getStepIndex()==3){
                    this.closeChannel(key);
                }
            }
        }else{  //key.isWritable()
            Sender sender = (Sender)key.attachment();
            if (sender.sendFully(channel)){
                System.out.println("Send: " + sender.getObject());
                //根据情况设定Sender的信息
                if (sender.getStepIndex()==1) {
                    //关注的事件类型不变，故不用重复注册，只需设置attachment
                    key.attach(new Sender(sender.getStepIndex()+1, "And u?"));
                }
                if (sender.getStepIndex()==2) {
                    channel.register(key.selector(), SelectionKey.OP_READ, new Receiver(sender.getStepIndex()+1, String.class));
                }
            }
        }
    }
}