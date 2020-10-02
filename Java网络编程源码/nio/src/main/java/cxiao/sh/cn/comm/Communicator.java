package cxiao.sh.cn.comm;

import java.nio.ByteBuffer;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

abstract public class Communicator {
    protected int stepIndex;
    protected ByteBuffer buffer;
    protected ByteBuffer countBuffer;
    protected boolean isCountDone = false;
    public int getStepIndex() {
        return stepIndex;
    }
}