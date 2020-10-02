package cxiao.sh.cn.comm;

import cxiao.sh.cn.utils.Utils;
import io.netty.buffer.ByteBuf;
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

//反序列化，将ByteBuf转成对象
@ChannelHandler.Sharable
public class UnSerializer extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //从ByteBuf->byte数组->对象
        ByteBuf buf = (ByteBuf)msg;
        byte[] bytes;
        if (buf.hasArray()){
             bytes = buf.array();
        }else{
            bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
        }
        Object obj = Utils.byteArrayToObjSerializable(bytes);
        ctx.fireChannelRead(obj);
        ReferenceCountUtil.release(msg);
    }
}