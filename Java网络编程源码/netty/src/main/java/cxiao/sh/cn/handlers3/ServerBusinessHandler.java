package cxiao.sh.cn.handlers3;

import cxiao.sh.cn.comm.SaveFileHandler;
import cxiao.sh.cn.comm.SendCmd;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ServerBusinessHandler extends SaveFileHandler {
    public ServerBusinessHandler(String savedFilePath) {
        super(savedFilePath);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端建立连接，开始会话...");
    }

    @Override
    protected void doSomethingElse(ChannelHandlerContext ctx) {
        //服务端会话操作：
        // 接收文件、发送文件、发送文件、接收文件

        //这里只需进行文件的发送及关闭通道！
        //文件的接收在父类的channelRead(.)中完成。
        if(this.recvFileTimes == 1) {
            String fPath = this.savedFileFolder + "xyz.mp4";
            ctx.channel().write(new SendCmd(fPath));

            fPath = this.savedFileFolder + "xyz.pdf";
            ctx.channel().write(new SendCmd(fPath));
        }
        if (this.recvFileTimes == 2) {
            //在读操作之后的通道关闭可直接进行
            ctx.channel().close();
            System.out.println("会话结束，服务端关闭连接！");
        }
    }
}