package cxiao.sh.cn.comm;

import cxiao.sh.cn.comm.att.Attachment;
import cxiao.sh.cn.comm.att.AttachmentForFile;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Receiver {

    //使用Future特性，用作对照，并不使用
    private static ByteBuffer recvBufferFully0(AsynchronousSocketChannel channel) throws Exception{
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        while (countBuffer.hasRemaining()) {
            channel.read(countBuffer).get();
        }

        ByteBuffer buffer = ByteBuffer.allocate(countBuffer.getInt(0));
        while (buffer.hasRemaining()) {
            channel.read(buffer).get();
        }

        return buffer;
    }

    //这是其他recvString(.)、recvObject(.)、recvFile(.)的基础
    //这个函数的功能是以异步的方式完成以下步骤：
    // 1. 接收一个整数，
    // 2. 构建一个缓冲区，大小为第一步的整数
    // 3. 把缓冲区读满
    private static ByteBuffer recvBufferFully(AsynchronousSocketChannel channel) throws Exception{
        Attachment att = new Attachment(new CountDownLatch(1), null);
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        //接收表示长度的4个字节
        channel.read(countBuffer, countBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer countBuffer) {
                //确保读满4个字节
                if (countBuffer.hasRemaining()){
                    channel.read(countBuffer, countBuffer, this);
                    return;
                }
                att.setBuffer(ByteBuffer.allocate(countBuffer.getInt(0)));
                channel.read(att.getBuffer(), att, new CompletionHandler<Integer, Attachment>() {
                    @Override
                    public void completed(Integer result, Attachment att) {
                        //确保读满数据缓冲区
                        if (att.getBuffer().hasRemaining()){
                            channel.read(att.getBuffer(), att, this);
                            return;
                        }
                        att.getEndLatch().countDown();
                    }
                    @Override
                    public void failed(Throwable exc, Attachment att) {
                        exc.printStackTrace();
                        att.getEndLatch().countDown();
                    }
                });
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
                att.getEndLatch().countDown();
            }
        });
        att.getEndLatch().await();
        return att.getBuffer();
    }

    //接收一个字符串
    public static String recvString(AsynchronousSocketChannel channel) throws Exception {
        ByteBuffer buffer =  recvBufferFully(channel);
        buffer.flip();
        byte[] bytes = buffer.array();
        return new String(bytes,"utf-8");
    }

    //接收一个对象，必须具有Serializable接口
    public static Object recvObjSerializable(AsynchronousSocketChannel channel) throws Exception{
        ByteBuffer buf = recvBufferFully(channel);
        buf.flip();
        byte[] bytes = buf.array();
        return Utils.byteArrayToObjSerializable(bytes);
    }

    //这个是同步函数
    //接收一个文件，并保存至参数filePath指定的位置
    //操作步骤：
    //1. 接收表示文件长度的8个字节
    //2. 分批接收文件的数据字节，保存至文件
    //3. 直至接收完指定的字节个数
    public static void recvFile(AsynchronousSocketChannel channel, String filePath) throws Exception {
        Path path = Paths.get(filePath);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,  StandardOpenOption.CREATE);
        //先接收一个长整数，表示总的字节数
        ByteBuffer countBuffer = ByteBuffer.allocate(8);
        CountDownLatch endHeader = new CountDownLatch(1);
        channel.read(countBuffer, countBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                //确保接收完8个字节
                if (countBuffer.hasRemaining()){
                    channel.read(countBuffer, countBuffer, this);
                    return;
                }
                endHeader.countDown();
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
                endHeader.countDown(); //避免发生异常时死锁
            }
        });
        //同步：必须等待总字节数接收完毕，再接收文件数据
        //因为多线程共用一个SocketChannel，而且有了总字节数才能做后续处理
        endHeader.await();
        ByteBuffer buffer = recvBufferFully(channel);
        buffer.flip();
        AttachmentForFile att = new AttachmentForFile(new CountDownLatch(1),buffer, 0L, countBuffer.getLong(0));
        //异步方式保存文件
        fileChannel.write(att.getBuffer(), att.getPosInFile(), att, new CompletionHandler<Integer, AttachmentForFile>() {
            @Override
            public void completed(Integer result, AttachmentForFile att) {
                att.setPosInFile(att.getPosInFile() + result);
                att.setRestByteCount(att.getRestByteCount() - result);
                //确保缓冲区内容全部写入文件
                if (att.getBuffer().hasRemaining()){
                    fileChannel.write(att.getBuffer(), att.getPosInFile(), att, this);
                    return;
                }
                //全部的字节写完了，结束函数recvFileFully()
                if (att.getRestByteCount() == 0){
                    att.getEndLatch().countDown();
                    return;
                }
                //尚未全部写完，则先读入，再次写入文件
                ByteBuffer buffer = null;
                try {
                    buffer = recvBufferFully(channel);
                }catch (Exception e){ e.printStackTrace();}
                buffer.flip();
                att.setBuffer(buffer);
                fileChannel.write(att.getBuffer(), att.getPosInFile(), att, this);
            }
            @Override
            public void failed(Throwable exc, AttachmentForFile att) {
                att.getEndLatch().countDown(); //避免出现异常时发生死锁
            }
        });
        att.getEndLatch().await();
        fileChannel.close();
    }
}