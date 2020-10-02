package cxiao.sh.cn.discovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfo {
    // 因为服务信息来自注册中心，所以这里必须用字符串，不能用Class<?>
    // interfName 与 version 构成联合主键
    private String interfName;
    private String version;
    private String address; // ip:port的格式
    private String serializingType;
}