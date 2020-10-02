package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client4 extends Client{
    public static void main(String[] args) throws Exception{

        ExecutorService fixPool = Executors.newCachedThreadPool();
        int con = 500;
        CyclicBarrier cb = new CyclicBarrier(con);
        for (int i=0;i<con;i++) {
            fixPool.execute(
                    ()->{
                        try {
                            Client4 client = new Client4();
                            cb.await();
                            client.communicate();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();

    }

    public Client4() throws Exception{
        super("127.0.0.1",8001);
    }

    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception {
        Student student = StuRepository.getStudent();
        channel.register(selector, SelectionKey.OP_WRITE, new Sender(0,student));
    }

    @Override
    protected void session(SelectionKey key) throws Exception {
        // 客户端会话操作：写、读、读、写(列表)
        SocketChannel channel = (SocketChannel) key.channel();
        if (key.isReadable()) {
            Receiver receiver = (Receiver)key.attachment();
            if (receiver.recvFully(channel)){
                System.out.println("接收对象：" + receiver.getObject());
                if (receiver.getStepIndex()==1){
                    //不用重复注册
                    key.attach(new Receiver(receiver.getStepIndex()+1, Object.class));
                }
                if (receiver.getStepIndex()==2) {
                    channel.register(key.selector(), SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex()+1,StuRepository.getStudents()));
                }
            }
        }else{
            Sender sender = (Sender) key.attachment();
            if (sender.sendFully(channel)){
                System.out.println("发送对象: " + sender.getObject());
                if (sender.getStepIndex()==0) {
                    channel.register(key.selector(), SelectionKey.OP_READ, new Receiver(sender.getStepIndex()+1,Object.class));
                }
                if (sender.getStepIndex()==3) {
                    this.closeChannel(key);
                }
            }
        }
    }
}