package cxiao.sh.cn.comm;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Sender extends Communicator {
    //要发送的对象
    private Object obj;

    public Sender(int i, Object object) throws Exception{
        this.obj = object;
        //对象->ByteBuffer
        buffer = objToByteBuffer(obj);
        countBuffer = ByteBuffer.allocate(4);
        countBuffer.putInt(0, buffer.capacity());
        this.stepIndex = i;
    }
    public Object getObject() {
        return obj;
    }

    // 若全部发送，则返回true；否则返回false，有待下一次继续发送
    public boolean sendFully(SocketChannel channel) throws Exception{
        if (!isCountDone){
            isCountDone = sendBufferFully(channel, countBuffer);
            return false;
        }
        return sendBufferFully(channel, buffer);
    }

    private boolean sendBufferFully(SocketChannel channel, ByteBuffer buf) throws Exception{
        channel.write(buf);
        return !buf.hasRemaining();
    }

    private ByteBuffer objToByteBuffer(Object object) throws Exception{
        //字符串的序列化不同于一般对象的序列化
        if (object instanceof String){
            String st = (String)object;
            return ByteBuffer.wrap(st.getBytes("utf-8"));
        }
        if (object instanceof Serializable){
            return ByteBuffer.wrap(Utils.objSerializableToByteArray(object));
        }
        throw new Exception("没有序列化接口，无法转成ByteBuffer!");
    }
}