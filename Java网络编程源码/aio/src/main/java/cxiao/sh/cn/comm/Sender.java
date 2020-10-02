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

public class Sender {
    //这个是用Future方式，像同步方式一样编写出异步执行
    //作为对照，并不使用
    private static void sendBufferFully0(AsynchronousSocketChannel channel, ByteBuffer buffer) throws Exception {

        // buffer用于存放要发送的字节列表
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        countBuffer.putInt(buffer.capacity());
        countBuffer.flip();

        while (countBuffer.hasRemaining()) {
            channel.write(countBuffer).get();
        }

        while (buffer.hasRemaining()) {
            channel.write(buffer).get();
        }
    }

    // 用回调函数的方式，完整地发送一个Buffer
    // 异步方式代码+CountDownLatch 实现同步效果，
    // 这是个阻塞式函数
    // 当这个函数返回时，Buffer已经整体发送，
    // 若这个函数处于阻塞状态，必然是还处于发送中。
    private static void sendBufferFully(AsynchronousSocketChannel channel, ByteBuffer buf) throws Exception{
        Attachment att = new Attachment(new CountDownLatch(1), buf);
        // countBuffer用于存放要发送的字节列表
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        countBuffer.putInt(buf.capacity());
        countBuffer.flip();
        //先发送表示缓冲区长度的4个字节
        channel.write(countBuffer, countBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer countBuffer) {
                if (countBuffer.hasRemaining()) {
                    channel.write(countBuffer, countBuffer, this);
                    return;
                }
                //表示长度的4个字节已发送完毕，再发送缓冲区内容
                channel.write(att.getBuffer(), att, new CompletionHandler<Integer, Attachment>() {
                    @Override
                    public void completed(Integer result, Attachment att) {
                        if (att.getBuffer().hasRemaining()) {
                            channel.write(att.getBuffer(), att, this);
                            return;
                        }
                        //缓冲区已发送完毕
                        att.getEndLatch().countDown();
                    }
                    @Override
                    public void failed(Throwable exc, Attachment att) {
                        try{
                            channel.close();
                        }catch (Exception e){}
                        exc.printStackTrace();
                        att.getEndLatch().countDown();
                    }
                });
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try{
                    channel.close();
                }catch (Exception e){}
                exc.printStackTrace();
                att.getEndLatch().countDown();
            }
        });
        //等待缓冲区发送完毕
        att.getEndLatch().await();
    }

    //阻塞式同步函数
    //发送完成才返回，否则阻塞
    public static void sendString(AsynchronousSocketChannel channel, String msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("utf-8"));
        sendBufferFully(channel, buffer);
    }

    //阻塞式同步函数
    //发送完成才返回，否则阻塞
    public static void sendObjSerializable(AsynchronousSocketChannel channel, Object obj) throws Exception{
        ByteBuffer buf = ByteBuffer.wrap(Utils.objSerializableToByteArray(obj));
        sendBufferFully(channel, buf);
    }

    //阻塞式同步函数
    //发送完成才返回，否则阻塞
    public static void sendFile(AsynchronousSocketChannel channel, String filePath) throws Exception {
        Path path = Paths.get(filePath);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        long restByteCount = fileChannel.size();
        // countBuffer用于存放要发送的文件长度值
        ByteBuffer countBuffer = ByteBuffer.allocate(8);
        countBuffer.putLong(restByteCount);
        countBuffer.flip();
        CountDownLatch endHeader = new CountDownLatch(1);
        //先发送总字节数
        channel.write(countBuffer, countBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (countBuffer.hasRemaining()) {
                    channel.write(countBuffer, countBuffer, this);
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
        //同步：保证先发送总字节数，再发送数据字节
        //因为共用同一个SocketChannel
        endHeader.await();
        //从文件读取数据再发送
        int buffSize = 1024*1024;
        if (buffSize > restByteCount) {
            buffSize = (int) restByteCount;
        }
        AttachmentForFile att = new AttachmentForFile(new CountDownLatch(1), ByteBuffer.allocate(buffSize), 0, restByteCount);
        fileChannel.read(att.getBuffer(), att.getPosInFile(), att, new CompletionHandler<Integer, AttachmentForFile>() {
            @Override
            public void completed(Integer result, AttachmentForFile att) {
                att.setPosInFile(att.getPosInFile() + result);
                att.setRestByteCount(att.getRestByteCount() - result);
                //缓冲区未满，继续从文件填入
                if (att.getBuffer().hasRemaining()) {
                    fileChannel.read(att.getBuffer(), att.getPosInFile(), att, this);
                    return;
                }
                //缓冲区已满，先写出至socketChannel
                att.getBuffer().flip();
                try {
                    sendBufferFully(channel, att.getBuffer());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //检查是否已经到达文件末尾，
                //已经到达文件末尾，则当前函数sendFileFully(.)结束
                if (att.getRestByteCount() == 0) {
                    att.getEndLatch().countDown();
                    return;
                }
                //还未到达文件末尾，则清空缓冲区，再次填写缓冲区
                att.getBuffer().clear();
                if (att.getBuffer().capacity() > att.getRestByteCount()) {
                    att.setBuffer(ByteBuffer.allocate((int) att.getRestByteCount()));
                }
                fileChannel.read(att.getBuffer(), att.getPosInFile(), att, this);
            }
            @Override
            public void failed(Throwable exc, AttachmentForFile att) {
                exc.printStackTrace();
                att.getEndLatch().countDown(); //避免发生异常时死锁
            }
        });
        //等待文件发送完成
        att.getEndLatch().await();
        fileChannel.close();
    }
}