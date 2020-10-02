package cxiao.sh.cn.comm;

import cxiao.sh.cn.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

abstract public class SaveFileHandler extends ChannelInboundHandlerAdapter {
    protected int recvFileTimes = 0; //统计接收文件的次数
    FileInfo fInfo;
    FileOutputStream fout;
    FileChannel fcout;
    long bytesToWrite;
    protected String savedFileFolder;

    public SaveFileHandler(String savedFileFolder){
        this.savedFileFolder = savedFileFolder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        int segmentNo = buf.readInt();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ReferenceCountUtil.release(msg);
        if (segmentNo==0){
            fInfo = (FileInfo) Utils.byteArrayToObjSerializable(bytes);
            String fName = fInfo.getFileName();
            bytesToWrite = fInfo.getFileLength();
            fInfo.setFileName(savedFileFolder + new Random().nextInt(10) + "-" + fName);

            System.out.println("开始接收文件：" + fName);
            fout = new FileOutputStream(new File(fInfo.getFileName()));
            fcout = fout.getChannel();

            return;
        }

        //数据段的处理
        while (buffer.hasRemaining()){
            fcout.write(buffer);
        }
        bytesToWrite -= buffer.capacity();

        if (bytesToWrite == 0){
            fcout.close();
            fout.close();
            System.out.println("保存文件完成！" + fInfo.getFileName());
            recvFileTimes++;
            doSomethingElse(ctx);

        }

    }
    abstract protected void doSomethingElse(ChannelHandlerContext ctx);
}