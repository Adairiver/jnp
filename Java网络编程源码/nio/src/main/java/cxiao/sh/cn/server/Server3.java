package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.*;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server3 extends Server{
    public static void main(String[] args) throws Exception{
        Server3 server = new Server3();
        server.service();
    }
    public Server3() throws Exception{
        super(8001);
    }
    @Override
    protected void registerOnConnected(SocketChannel channel, Selector selector) throws Exception {
        channel.register(selector, SelectionKey.OP_READ, new Receiver(0, String.class));
    }
    @Override
    protected void session(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        Communicator comm = (Communicator)key.attachment();
        if (key.isReadable()) {
            // 接收文件名。
            if (comm.getStepIndex()==0) {
                Receiver receiver = (Receiver) comm;
                if (receiver.recvFully(channel)){
                    String fileName = (String)receiver.getObject();
                    System.out.println("接收到文件名：" + fileName);
                    String filePath = "D:\\ServerFiles\\FromClient-" + fileName;
                    // 下一次的操作还是READ，只需修改Attachment，不用重复注册
                    key.attach(new FileReceiver(receiver.getStepIndex() + 1, filePath));
                }
            }
            // 接收文件。
            if (comm.getStepIndex()==1) {
                FileReceiver receiver = (FileReceiver) comm;
                if (receiver.recvFully(channel)) {
                    String s = receiver.getFilePath();
                    System.out.println("接收文件完毕，保存至 " + s);
                    // 下一次操作是 WRITE，需要重新注册
                    channel.register(key.selector(), SelectionKey.OP_WRITE, new Sender(receiver.getStepIndex() + 1, "xyz.mp4"));
                }
            }
        }else{
            // 发送文件名。
            if (comm.getStepIndex()==2){
                Sender sender = (Sender) comm;
                if (sender.sendFully(channel)){
                    System.out.println("发送文件名：" + sender.getObject());
                    String filePath = "D:\\ServerFiles\\" + sender.getObject();
                    //下一次还是 WRITE，不需要重复注册
                    key.attach(new FileSender(sender.getStepIndex() + 1, filePath));
                }
            }
            //  发送文件。
            if (comm.getStepIndex()==3){
                FileSender sender = (FileSender) key.attachment();
                if (sender.sendFully(channel)) {
                    System.out.println("发送文件成功：" + sender.getFilePath());
                    this.closeChannel(key);
                }
            }
        }
    }
}