package cxiao.sh.cn.handlers2;

import cxiao.sh.cn.comm.entity.StuRepository;
import cxiao.sh.cn.comm.entity.Student;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@ChannelHandler.Sharable
public class ServerBusinessHandler extends ChannelInboundHandlerAdapter {
    int stepIndex = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端建立连接，开始会话...");
        Student stu = StuRepository.getStudent();
        ctx.channel().write(stu);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Student st = (Student) msg;
        System.out.println("成功接收： " + st);
        ReferenceCountUtil.release(msg);
        stepIndex++;
        if (stepIndex == 2){
            List<Student> list = StuRepository.getStudents();
            ChannelFuture future = ctx.channel().write(list);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        ctx.channel().close();
                        System.out.println("会话结束，服务端关闭连接！");
                    }
                }
            });
        }
    }
}