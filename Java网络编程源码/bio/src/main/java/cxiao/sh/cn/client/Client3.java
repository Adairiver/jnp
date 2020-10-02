package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.Student;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client3 extends Client1{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();
        int concurrentCount = 50;
        //用多线程模拟多客户端并发通信
        for (int i=0;i<concurrentCount;i++) {
            fixPool.execute(
                    ()->{
                        Client3 client = new Client3();
                        client.communicate();
                    }
            );
        }
        fixPool.shutdown();
    }
    public Client3() {
        super("127.0.0.1",8001);
    }
    @Override
    protected void session(Socket socket) throws Exception {
        Student stu = (Student)Receiver.recvObject(socket);
        System.out.println("接收对象: " + stu);
        Sender.sendObject(socket, stu);
        System.out.println("发送对象: " + stu);
        Sender.sendObject(socket, stu);
        System.out.println("发送对象: " + stu);
        List<Student> stuList = (List<Student>)Receiver.recvObject(socket);
        System.out.println("接收列表: " + stuList);
    }
}