package cxiao.sh.cn.server;

import cxiao.sh.cn.common.protocol.*;
import cxiao.sh.cn.register.ServiceObject;
import cxiao.sh.cn.register.ServiceRegister;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
public class RequestHandler{
    private ServiceRegister serviceRegister;
    public Response handleRequest(Request req) throws Exception{
        System.out.println("req=" + req);
        // 查找服务对象
        ServiceObject so = this.serviceRegister.getServiceObject(req.getServiceName(), req.getVersion());
        System.out.println(so);
        Response rsp = null;
        if (so == null) {
            rsp = new Response(Status.NOT_FOUND);
        } else {
            // 反射调用对应的过程方法
            try {
                Method m = so.getInterf().getMethod(req.getMethod(), req.getPrameterTypes());
                Object returnValue = m.invoke(so.getObj(), req.getParameters());
                rsp = new Response(Status.SUCCESS);
                rsp.setReturnValue(returnValue);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                rsp = new Response(Status.ERROR);
                rsp.setException(e);
            }
        }
        return rsp;
    }

}