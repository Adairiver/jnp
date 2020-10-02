package cxiao.sh.cn.comm.att;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
public class AttachmentForFile extends Attachment {
    private long posInFile;
    private long restByteCount;
    public AttachmentForFile(CountDownLatch endLatch, ByteBuffer buffer, long posInFile, long restByteCount) {
        this.endLatch = endLatch;
        this.buffer = buffer;
        this.posInFile = posInFile;
        this.restByteCount = restByteCount;
    }
}