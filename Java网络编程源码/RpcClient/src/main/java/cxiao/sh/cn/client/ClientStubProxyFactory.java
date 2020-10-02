package cxiao.sh.cn.client;

import cxiao.sh.cn.common.serializable.IClientSerializer;
import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;
import cxiao.sh.cn.discovery.ServiceInfo;
import cxiao.sh.cn.discovery.ServiceInfoDiscoverer;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
public class ClientStubProxyFactory {
    private ServiceInfoDiscoverer sid;
    private NetClient<Request,Response> netClient;
    private Map<String, IClientSerializer<Request,Response>> supportingSerializers;
    //key的形式为 interfName:version
    private Map<String, Object> proxyCache = new HashMap<>();
    public <T> T getProxy(Class<T> interf, String version) {
        String key = interf.getName() + ":" + version;
        T obj = (T) this.proxyCache.get(key);
        if (obj == null) {
            obj = (T) Proxy.newProxyInstance(interf.getClassLoader(),
                            new Class<?>[] { interf },
                            new ClientStubInvocationHandler(interf, version));
            this.proxyCache.put(key, obj);
        }
        return obj;
    }
    private class ClientStubInvocationHandler implements InvocationHandler {
        private Class<?> interf;
        private String version;
        public ClientStubInvocationHandler(Class<?> interf, String version) {
            super();
            this.interf = interf;
            this.version = version;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return proxy.getClass().toString();
            }
            if (method.getName().equals("hashCode")) {
                return 0;
            }
            // 1. 获得服务信息
            String serviceName = this.interf.getName();
            List<ServiceInfo> sinfos = sid.getServiceInfo(serviceName, this.version);
            if (sinfos == null || sinfos.size() == 0) {
                throw new Exception("远程服务不存在！");
            }
            // 2. 随机选择一个服务提供者（软负载均衡）
            ServiceInfo sinfo = sinfos.get(new Random().nextInt(sinfos.size()));
            // 3. 构造Request对象
            Request req = new Request();
            req.setServiceName(sinfo.getInterfName());
            req.setMethod(method.getName());
            req.setPrameterTypes(method.getParameterTypes());
            req.setParameters(args);
            req.setVersion(sinfo.getVersion());
            // 4. 获得对应的序列化器
            IClientSerializer<Request, Response> serializing = supportingSerializers.get(sinfo.getSerializingType());
            // 5. 发送请求并得到应答
            Response rsp = netClient.sendRequest(req, sinfo.getAddress(), serializing);
            if (rsp.getException() != null) {
                throw rsp.getException();
            }
            return rsp.getReturnValue();
        }
    }
}