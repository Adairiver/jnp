package cxiao.sh.cn.comm;

import cxiao.sh.cn.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.ToLongBiFunction;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class SegFileHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        final SendCmd sendCmd = (SendCmd)msg;
        String fPath = sendCmd.getFilePath();
        System.out.println("开始发送文件："+fPath);

        String fName = fPath.substring(fPath.lastIndexOf("\\")+1);
        File f = new File(fPath);
        long countBytesToSend = f.length();
        FileInputStream fin = new FileInputStream(fPath);
        FileChannel fcin = fin.getChannel();

        FileInfo fInfo = new FileInfo(fName,countBytesToSend);
        int bufSize = 1024*1024;
        if (bufSize > countBytesToSend){
            bufSize = (int)countBytesToSend;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        int segmentNo = 0;
        //将 segmentNo 和 fInfo 组成 ByteBuf，作为第0块发送
        byte[] bytes = Utils.objSerializableToByteArray(fInfo);
        ByteBuf buf = Unpooled.buffer(4 + bytes.length,4 + bytes.length);
        buf.writeInt(segmentNo);
        buf.writeBytes(bytes);
        //此处可以不加 Listener
        ctx.write(buf).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    System.out.println(fPath + " 第0号帧 发送成功！");
                }
            }
        });
        segmentNo++;

        //计算片段的总块数
        ToLongBiFunction<Long, Long> function = (a, b) -> {return a%b==0 ? (a/b):(a/b)+1;};
        final long segmentCount = function.applyAsLong(countBytesToSend, (long)bufSize);
        //每块文件片段发送完成后将执行此公用监听器
        ChannelFutureListener listener = new ChannelFutureListener() {
            long restSegments = segmentCount;
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println(fPath + " 第" + (segmentCount-restSegments+1) + "块文件片段 发送成功！");
                    restSegments--;
                    if (restSegments == 0) {
                        System.out.println("文件发送完成！" + fPath);
                        //如果需要关闭通道
                        if (sendCmd.isCloseAfterSending()) {
                            ctx.channel().close();
                            System.out.println("会话结束，客户端关闭连接！");
                        }
                    }
                }
            }
        };

        //循环发送文件的多个片段
        while (countBytesToSend > 0){
            if (bufSize > countBytesToSend){
                bufSize = (int)countBytesToSend;
                buffer = ByteBuffer.allocate(bufSize);
            }
            //写模式的buffer
            buffer.clear();

            // 从文件读入数据至缓冲区，读满缓冲区
            while (buffer.hasRemaining()){
                fcin.read(buffer);
            }

            buffer.flip(); //读模式的buffer

            //ctx.write(.)是异步的，因此这里不使用.wrappedBuffer(.)构造ByteBuf
            //避免可能发生的ByteBuf访问冲突
            //ByteBuffer segNoBuf= ByteBuffer.allocate(4);
            //segNoBuf.putInt(0,segmentNo);
            //buf = Unpooled.wrappedBuffer(segNoBuf, buffer);

            // 将 segmentNo 和 文件缓冲区 组成 ByteBuf，发送
            buf = Unpooled.buffer(4 + buffer.capacity(),4 + buffer.capacity());
            buf.writeInt(segmentNo);
            buf.writeBytes(buffer);
            //发送一块文件片段，当发送完成时回调listener
            ChannelFuture future = ctx.write(buf);
            future.addListener(listener);
            //因为ctx.write(.)是异步操作，不会阻塞等待。
            //随即开始下一块文件片段的发送
            countBytesToSend -= buffer.capacity();
            segmentNo++;
        }
        //关闭文件
        fcin.close();
        fin.close();
    }
}