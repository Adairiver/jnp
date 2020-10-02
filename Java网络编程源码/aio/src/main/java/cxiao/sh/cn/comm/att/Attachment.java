package cxiao.sh.cn.comm.att;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    protected CountDownLatch endLatch;
    protected ByteBuffer buffer;

}