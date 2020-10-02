package cxiao.sh.cn.discovery;

import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public interface ServiceInfoDiscoverer {
    List<ServiceInfo> getServiceInfo(String interfName, String version);
}
