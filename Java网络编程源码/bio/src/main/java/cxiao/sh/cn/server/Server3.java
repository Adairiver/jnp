package cxiao.sh.cn.server;

import cxiao.sh.cn.comm.Receiver;
import cxiao.sh.cn.comm.Sender;
import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;
import java.net.Socket;
import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Server3 extends Server1{
    public static void main(String[] args) {
        Server3 server = new Server3();
        server.service();
    }
    public Server3(){
        super(8001);
    }
    @Override
    protected void session(Socket socket) throws Exception {
        Student stu = StuRepository.getStudent();
        Sender.sendObject(socket, stu);
        System.out.println("发送对象: " + stu);
        stu = (Student)Receiver.recvObject(socket);
        System.out.println("接收对象: " + stu);
        stu = (Student)Receiver.recvObject(socket);
        System.out.println("接收对象: " + stu);
        List<Student> stuList = StuRepository.getStudents();
        Sender.sendObject(socket, stuList);
        System.out.println("发送列表: " + stuList);
    }
}