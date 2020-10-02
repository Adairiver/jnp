package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.*;
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

public class Client3 extends Client{
    public static void main(String[] args) throws Exception{
        ExecutorService fixPool = Executors.newCachedThreadPool();
        // 由于是在一台机器上模拟多客户端，为了避免文件访问冲突，只用一个客户端
        for (int i=0;i<1;i++) {
            fixPool.execute(
                    ()->{
                        try {
                            Client3 client = new Client3();
                            client.communicate();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client3() throws Exception{
        super("127.0.0.1",8001);
    }
    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception {
        channel.register(selector, SelectionKey.OP_WRITE, new Sender(0,"abc.mp4"));
    }
    @Override
    protected void session(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        Communicator comm = (Communicator)key.attachment();
        if (key.isReadable()) {
            // 接收文件名。
            if (comm.getStepIndex()==2) {
                Receiver receiver = (Receiver) comm;
                if (receiver.recvFully(channel)){
                    String fileName = (String) receiver.getObject();
                    System.out.println("接收到文件名：" + fileName);
                    String filePath = "D:\\ClientFiles\\FromServer-" + fileName;
                    // 下一次的操作还是READ，因此只需修改Attachment，不用重复注册
                    key.attach(new FileReceiver(receiver.getStepIndex() + 1, filePath));
                }
            }
            // 接收文件。
            if (comm.getStepIndex()==3) {
                FileReceiver receiver = (FileReceiver) comm;
                if (receiver.recvFully(channel)) {
                    String s = receiver.getFilePath();
                    System.out.println("接收文件完毕，保存至 " + s);
                    this.closeChannel(key);
                }
            }
        }else{
            // 发送文件名。
            if (comm.getStepIndex()==0){
                Sender sender = (Sender) comm;
                if (sender.sendFully(channel)){
                    System.out.println("发送文件名：" + sender.getObject());
                    String filePath = "D:\\ClientFiles\\" + sender.getObject();
                    // 不用重复注册
                    key.attach(new FileSender(sender.getStepIndex() + 1, filePath));
                }
            }
            //  发送文件。
            if (comm.getStepIndex()==1){
                FileSender sender = (FileSender) key.attachment();
                if (sender.sendFully(channel)) {
                    System.out.println("发送文件成功：" + sender.getFilePath());
                    channel.register(key.selector(), SelectionKey.OP_READ, new Receiver(sender.getStepIndex() + 1, String.class));
                }
            }
        }
    }
}