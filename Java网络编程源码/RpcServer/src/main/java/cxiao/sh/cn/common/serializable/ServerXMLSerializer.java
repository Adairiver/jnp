package cxiao.sh.cn.common.serializable;

import com.thoughtworks.xstream.XStream;
import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ServerXMLSerializer implements IServerSerializer<Request, Response> {
    private XStream xStream;
    public ServerXMLSerializer(){
        xStream = new XStream();
        xStream.alias(Request.class.getSimpleName(), Request.class);
        xStream.alias(Response.class.getSimpleName(), Response.class);
    }
    @Override
    public byte[] marshalling(Response response) throws Exception {
        String xml = xStream.toXML(response);
        return xml.getBytes("utf-8");
    }
    @Override
    public Request unmarshalling(byte[] data) throws Exception {
        String xml = new String(data, "utf-8");
        return (Request) xStream.fromXML(xml);
    }
}