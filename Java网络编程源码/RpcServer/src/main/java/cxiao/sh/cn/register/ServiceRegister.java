package cxiao.sh.cn.register;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ServiceRegister {
    private ConcurrentLinkedQueue<ServiceObject> serviceDB = new ConcurrentLinkedQueue<>();
    // IP就是服务器本身的IP！
    public void register(ServiceObject so, String protocol, int port) throws Exception{
        if (so == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        this.serviceDB.add(so);
        // protocol 与 port 在真正注册时使用，这里不作注册。
    }
    public ServiceObject getServiceObject(String interfName, String version){
        for(ServiceObject so:serviceDB){
            if (so.getInterf().getName().equals(interfName) && so.getVerseion().equals(version)){
                return so;
            }
        }
        return null;
    }
}
