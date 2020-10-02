package cxiao.sh.cn.handlers3;

import cxiao.sh.cn.comm.SaveFileHandler;
import cxiao.sh.cn.comm.SendCmd;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ClientBusinessHandler extends SaveFileHandler {
    public ClientBusinessHandler(String savedFilePath) {
        super(savedFilePath);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端建立连接，开始会话...");
        String fPath = "D:\\ClientFiles\\abc.mp4";
        ctx.channel().write(new SendCmd(fPath));
    }

    @Override
    protected void doSomethingElse(ChannelHandlerContext ctx) {
        //客户端会话操作：
        // 发送文件、接收文件、接收文件、发送文件

        //这里只需进行文件的发送！
        //文件的接收在父类的channelRead(.)中完成。
        if(this.recvFileTimes==2) {
            String fPath = this.savedFileFolder + "abc.docx";
            //因为要在下列的“写”之后关闭通道，
            //所以关闭通道及其结束提示必须放在Listener里去做，
            //SendCmd(.)第二个参数为true指示发送成功之后关闭通道
            ctx.channel().write(new SendCmd(fPath,true));
        }
    }
}