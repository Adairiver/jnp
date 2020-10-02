package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server2 extends Server{
    public static void main(String[] args) throws Exception {
        Server2 server = new Server2();
        server.service();
    }

    public Server2() throws Exception{
        super(8001);
    }

    @Override
    protected void session(AsynchronousSocketChannel channel) throws Exception {
        // 底层是异步，效果是同步
        Student stu;
        stu = (Student) Receiver.recvObjSerializable(channel);
        System.out.println("接收到对象:" + stu);

        stu = StuRepository.getStudent();
        Sender.sendObjSerializable(channel, stu);
        System.out.println("发送了对象：" + stu);

        stu = StuRepository.getStudent();
        Sender.sendObjSerializable(channel, stu);
        System.out.println("发送了对象：" + stu);

        List<Student> students = (List<Student>)Receiver.recvObjSerializable(channel);
        System.out.println("接收了列表：" + students);

    }
}