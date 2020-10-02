package cxiao.sh.cn.client;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client2 extends Client{
    public static void main(String[] args) {
        ExecutorService fixPool = Executors.newCachedThreadPool();
        //模拟10个客户端并发操作
        for (int i = 0; i < 10; i++) {
            fixPool.execute(
                    () -> {
                        try {
                            Client2 client = new Client2();
                            client.communicate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        }
        fixPool.shutdown();
    }

    public Client2(){
        super("127.0.0.1", 8001);
    }

    @Override
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步

        Student stu = StuRepository.getStudent();
        Sender.sendObjSerializable(channel, stu);
        System.out.println("发送了对象：" + stu);

        stu = (Student) Receiver.recvObjSerializable(channel);
        System.out.println("接收到对象：" + stu);

        stu = (Student) Receiver.recvObjSerializable(channel);
        System.out.println("接收到对象：" + stu);

        List<Student> list = StuRepository.getStudents();
        Sender.sendObjSerializable(channel, list);
        System.out.println("发送了列表：" + list);
    }
}