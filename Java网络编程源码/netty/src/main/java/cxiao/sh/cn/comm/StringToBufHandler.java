package cxiao.sh.cn.comm;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

//出站，把String转成ByteBuf
@ChannelHandler.Sharable
public class StringToBufHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String str = (String)msg;
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("成功发送： " + str);
                }
            }
        });
        byte[] bytes = str.getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        ReferenceCountUtil.release(msg);
        ctx.write(buf, promise);
    }
}