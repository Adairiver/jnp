package cxiao.sh.cn.comm;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class FileReceiver extends Communicator{
    private FileChannel fileChannel;
    private long restBytesToRead;
    private String filePath;     //文件路径
    private int bufSize = 2048*2;
    private boolean newRound;
    public FileReceiver(int i, String filePath) throws Exception{
        this.filePath = filePath;
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        fileChannel = fos.getChannel();
        countBuffer = ByteBuffer.allocate(8);
        buffer = ByteBuffer.allocate(bufSize);
        this.filePath = filePath;
        this.stepIndex = i;
        newRound = true;
    }
    public String getFilePath() {
        return filePath;
    }
    public boolean recvFully(SocketChannel channel) throws Exception{
        if (!isCountDone){
            isCountDone = recvBufferFully(channel, countBuffer);
            if (isCountDone){
                restBytesToRead = countBuffer.getLong(0);
            }
            return false;
        }
        if (newRound) {
            buffer.clear(); //写模式
            newRound = false;
            if (bufSize > restBytesToRead) {
                bufSize = (int) restBytesToRead;
                buffer = ByteBuffer.allocate(bufSize);
            }
        }
        boolean t = recvBufferFully(channel, buffer); //把buffer写满为止
        if (t){
            buffer.flip();  //读模式
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
            }
            restBytesToRead -= buffer.capacity();
            if (restBytesToRead > 0){
                t = false;
                newRound = true;
            }else{
                fileChannel.close();
            }
        }
        return  t;
    }
    private boolean recvBufferFully(SocketChannel channel, ByteBuffer buf) throws Exception{
        channel.read(buf);
        return !buf.hasRemaining();
    }
}