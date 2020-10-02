package cxiao.sh.cn.handlers1;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@ChannelHandler.Sharable
public class ServerBusinessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端建立连接，开始会话...");
    }
    int stepIndex = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String)msg;
        System.out.println("成功接收： " + str);
        ReferenceCountUtil.release(msg);
        stepIndex++;
        if (stepIndex==1){
            str = "I am fine, thank you!";
            ctx.channel().write(str);
            str = "And you?";
            ctx.channel().write(str);
        }
        if (stepIndex == 2){
            ctx.channel().close();
            System.out.println("会话结束，服务端关闭连接！");
        }
    }
}