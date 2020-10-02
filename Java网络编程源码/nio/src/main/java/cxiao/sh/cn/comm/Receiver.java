package cxiao.sh.cn.comm;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Receiver extends Communicator {
    //存储所接收的对象
    private Object obj;
    private Class<?> clazz;

    public Receiver(int i, Class<?> clazz){
        this.clazz = clazz;
        countBuffer = ByteBuffer.allocate(4);
        buffer = null;
        obj = null;
        this.stepIndex = i;
    }

    public boolean recvFully(SocketChannel channel) throws Exception{
        if (!isCountDone){
            isCountDone = recvBufferFully(channel, countBuffer);
            return false;
        }
        if (buffer == null){
            buffer = ByteBuffer.allocate(countBuffer.getInt(0));
        }
        return recvBufferFully(channel, buffer);
    }

    public Object getObject() throws Exception{

        //将ByteBuffer内部的byte[]反序列化得到对象，要求对象实现了Serializable
        if (!buffer.hasRemaining()){
            buffer.flip();
            obj = byteBufferToObject(buffer);
        }
        return obj;
    }

    private boolean recvBufferFully(SocketChannel channel, ByteBuffer buf) throws Exception{
        channel.read(buf);
        return !buf.hasRemaining();
    }

    private Object byteBufferToObject(ByteBuffer buffer) throws Exception{

        byte[] bytes = buffer.array();
        if (clazz.equals(String.class)){
            return new String(bytes,"utf-8");
        }
        return Utils.byteArrayToObjSerializable(bytes);
    }
}