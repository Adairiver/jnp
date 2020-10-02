package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server2 extends Server{
    public static void main(String[] args) throws Exception{
        Server2 server = new Server2();
        server.service();
    }
    public Server2() throws Exception{
        super(8001);
    }
    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception {
        channel.register(selector, SelectionKey.OP_READ, new Receiver(0, Student.class));
    }
    @Override
    protected void session(SelectionKey key) throws Exception {
        // 服务端会话操作：读、写、写、读(列表)
        SocketChannel channel = (SocketChannel) key.channel();
        if (key.isReadable()) {
            Receiver receiver = (Receiver)key.attachment();
            if (receiver.recvFully(channel)){
                Object obj = receiver.getObject();
                if (obj.getClass().equals(Student.class)) {
                    System.out.println("接收单个学生: " + obj);
                }
                if (obj instanceof List){
                    System.out.println("接收学生列表：" + obj);
                }
                if (receiver.getStepIndex()==0) {
                    channel.register(key.selector(), SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex()+1, StuRepository.getStudent()));
                }
                if (receiver.getStepIndex()==3){
                    this.closeChannel(key);
                }
            }
        }else {
            Sender sender = (Sender)key.attachment();
            if (sender.sendFully(channel)){
                System.out.println("发送单个学生: " + sender.getObject());
                //根据情况设定Sender的信息
                if (sender.getStepIndex()==1) {
                    //因为允许的操作相同，所以不用重复注册，只需修改attachment对象
                    key.attach(new Sender(sender.getStepIndex()+1, StuRepository.getStudent()));
                }
                if (sender.getStepIndex()==2) {
                    channel.register(key.selector(), SelectionKey.OP_READ, new Receiver(sender.getStepIndex()+1, List.class));
                }
            }
        }
    }
}