package cxiao.sh.cn.server;

import cxiao.sh.cn.common.serializable.IServerSerializer;
import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
public abstract class RpcServer {
    protected int port;
    protected String protocol;
    protected RequestHandler handler;
    protected IServerSerializer<Request, Response> serializer;
    //启动服务
    public abstract void start();
    //停止服务
    public abstract void stop();
}