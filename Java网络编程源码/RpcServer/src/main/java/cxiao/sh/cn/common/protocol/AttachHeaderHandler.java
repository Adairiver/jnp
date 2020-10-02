package cxiao.sh.cn.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

//给ByteBuf增加一个表示其数据长度的头部信息，4个字节
@ChannelHandler.Sharable
public class AttachHeaderHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        ByteBuf countBuf = Unpooled.buffer(4,4);
        countBuf.writeInt(buf.capacity());
        ByteBuf newBuf = Unpooled.wrappedBuffer(countBuf,buf);
        ctx.writeAndFlush(newBuf);
        ReferenceCountUtil.release(msg);
    }
}