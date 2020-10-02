package cxiao.sh.cn.provider;

import cxiao.sh.cn.common.business.HelloInterface;
import cxiao.sh.cn.common.business.StudentInterface;
import cxiao.sh.cn.common.protocol.*;
import cxiao.sh.cn.common.serializable.IServerSerializer;
import cxiao.sh.cn.common.serializable.ServerJavaSerializer;
import cxiao.sh.cn.common.serializable.ServerXMLSerializer;
import cxiao.sh.cn.common.util.PropertiesUtils;
import cxiao.sh.cn.provider.impl.HelloService1;
import cxiao.sh.cn.provider.impl.HelloService2;
import cxiao.sh.cn.provider.impl.HelloService3;
import cxiao.sh.cn.provider.impl.StudentService;
import cxiao.sh.cn.register.ServiceObject;
import cxiao.sh.cn.register.ServiceRegister;
import cxiao.sh.cn.server.NettyRpcServer;
import cxiao.sh.cn.server.RequestHandler;
import cxiao.sh.cn.server.RpcServer;

import java.util.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Provider {
    public static void main(String[] args) throws Exception {
        //所有的对象用相同的port
        int port = Integer.parseInt(PropertiesUtils.getProperties("rpc.port"));
        String serializingType = PropertiesUtils.getProperties("rpc.serializing");
        // 服务注册 (模拟)
        ServiceRegister sr = new ServiceRegister();
        List<ServiceObject> objects = Arrays.asList(
                new ServiceObject(HelloInterface.class, "1.0", new HelloService1()),
                new ServiceObject(HelloInterface.class, "2.0", new HelloService2()),
                new ServiceObject(HelloInterface.class, "3.0", new HelloService3()),
                new ServiceObject(StudentInterface.class, "1.0", new StudentService())
        );
        for (ServiceObject so:objects){
            sr.register(so, serializingType, port);
        }
        // 设置所支持的全部协议
        Map<String, IServerSerializer<Request, Response>> supportingSerializers = new HashMap<>();
        supportingSerializers.put("javas", new ServerJavaSerializer());
        supportingSerializers.put("xml", new ServerXMLSerializer());
        // 获取所设置的序列化格式
        IServerSerializer<Request, Response> serverSerializer = supportingSerializers.get(serializingType);
        RequestHandler reqHandler = new RequestHandler(sr);
        RpcServer server = new NettyRpcServer(port, serializingType, reqHandler, serverSerializer);
        server.start();
        System.in.read(); // 按任意键退出
        server.stop();
    }
}