package cxiao.sh.cn.handlers2;

import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ClientBusinessHandler extends ChannelInboundHandlerAdapter {
    int stepIndex = 0;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端建立连接，开始会话...");
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        stepIndex++;
        // 第一次接收，
        if (stepIndex==1) {
            Student st = (Student) msg;
            System.out.println("成功接收： " + st);
            ReferenceCountUtil.release(msg);

            st = StuRepository.getStudent();
            ctx.channel().write(st);

            st = StuRepository.getStudent();
            ctx.channel().write(st);
        }

        if (stepIndex == 2){
            List<Student> stList = (List<Student>) msg;
            System.out.println("成功接收： " + stList);
            ReferenceCountUtil.release(msg);

            ctx.channel().close();
            System.out.println("会话结束，客户端关闭连接！");
        }
    }
}