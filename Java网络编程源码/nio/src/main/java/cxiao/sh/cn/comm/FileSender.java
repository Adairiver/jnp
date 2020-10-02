package cxiao.sh.cn.comm;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class FileSender extends Communicator{
    private String filePath;
    private FileChannel fileChannel;
    private final int bufSize = 2000*2000;
    private boolean newRound;
    public FileSender(int i, String filePath) throws Exception{
        this.filePath = filePath;
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        fileChannel = fis.getChannel();
        buffer = ByteBuffer.allocate(bufSize);
        countBuffer = ByteBuffer.allocate(8);
        countBuffer.putLong(0, file.length());
        newRound = true;
        this.stepIndex = i;
    }
    public String getFilePath() {
        return filePath;
    }
    // 若全部发送，则返回true；否则返回false，有待下一次继续发送
    public boolean sendFully(SocketChannel channel) throws Exception{
        if (!isCountDone){
            isCountDone = sendBufferFully(channel, countBuffer);
            return false;
        }
        if (newRound){
            this.buffer.clear(); //将Buffer设为写模式
            fileChannel.read(this.buffer);
            newRound = false;
            this.buffer.flip();  //将Buffer设为读模式
        }
        boolean t = sendBufferFully(channel, buffer);
        if (t) {
            if (fileChannel.position() < fileChannel.size()) {
                t = false;
                newRound = true;
            }else{
                fileChannel.close();
            }
        }
        return t;
    }
    private boolean sendBufferFully(SocketChannel channel, ByteBuffer buf) throws Exception{
        channel.write(buf);
        return !buf.hasRemaining();
    }
}