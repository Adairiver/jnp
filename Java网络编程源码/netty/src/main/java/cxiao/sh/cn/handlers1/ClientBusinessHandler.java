package cxiao.sh.cn.handlers1;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@ChannelHandler.Sharable
public class ClientBusinessHandler extends ChannelInboundHandlerAdapter {
    int stepIndex = 0;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端建立连接，开始会话...");
        String s = "How are you?";
        ctx.channel().write(s);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String)msg;
        System.out.println("成功接收： " + str);
        ReferenceCountUtil.release(msg);
        stepIndex++;
        if (stepIndex == 2){
            str = "I'm fine, too!";
            ChannelFuture future = ctx.channel().write(str);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        ctx.channel().close();
                        System.out.println("会话结束，客户端关闭连接！");
                    }
                }
            });
        }
    }
}