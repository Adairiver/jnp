package cxiao.sh.cn.common.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
public class Request implements Serializable {
    private String serviceName;
    private String version;
    private String method;
    private Map<String, String> headers = new HashMap<String, String>();
    private Class<?>[] prameterTypes;
    private Object[] parameters;
    public String getHeader(String name) {
        return this.headers == null ? null : this.headers.get(name);
    }
    public void setHeader(String key, String value){
        this.headers.put(key, value);
    }
}