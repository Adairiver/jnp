package cxiao.sh.cn.comm;

import cxiao.sh.cn.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

//出站，把Object转成ByteBuf
@ChannelHandler.Sharable
public class Serializer extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("成功发送： " + msg);
                    ReferenceCountUtil.release(msg);
                }
            }
        });
        //把对象转成字节数组
        byte[] bytes = Utils.objSerializableToByteArray(msg);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        ctx.write(buf, promise);
    }
}