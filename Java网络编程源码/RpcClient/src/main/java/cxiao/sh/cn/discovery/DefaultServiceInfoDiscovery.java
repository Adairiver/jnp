package cxiao.sh.cn.discovery;

import cxiao.sh.cn.common.util.PropertiesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class DefaultServiceInfoDiscovery implements ServiceInfoDiscoverer {
    static String serializingType = PropertiesUtils.getProperties("rpc.serializing");
    static String address = "127.0.0.1:" + PropertiesUtils.getProperties("rpc.port");

    static List<ServiceInfo> serviceDB = Arrays.asList(
            new ServiceInfo("cxiao.sh.cn.common.business.HelloInterface", "1.0", address, serializingType),
            new ServiceInfo("cxiao.sh.cn.common.business.HelloInterface", "2.0", address, serializingType),
            new ServiceInfo("cxiao.sh.cn.common.business.HelloInterface", "3.0", address, serializingType),
            new ServiceInfo("cxiao.sh.cn.common.business.StudentInterface", "1.0", address, serializingType)
    );
    @Override
    public List<ServiceInfo> getServiceInfo(String interfName, String version) {
        List<ServiceInfo> list = new ArrayList<>();
        for(ServiceInfo sInfo : serviceDB){
            if (sInfo.getInterfName().equals(interfName) && sInfo.getVersion().equals(version)){
                list.add(sInfo);
            }
        }
        return list;
    }
}