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
public class Response implements Serializable {
    private Map<String, String> headers = new HashMap<>();
    private Status status;
    private Object returnValue;
    private Exception exception;
    public Response() { }
    public Response(Status status) {
        this.status = status;
    }
    public String getHeader(String name) {
        return this.headers == null ? null : this.headers.get(name);
    }
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }
}