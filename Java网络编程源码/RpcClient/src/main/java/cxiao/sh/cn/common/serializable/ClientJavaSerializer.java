package cxiao.sh.cn.common.serializable;

import cxiao.sh.cn.common.protocol.Request;
import cxiao.sh.cn.common.protocol.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class ClientJavaSerializer implements IClientSerializer<Request, Response>{
    @Override
    public byte[] marshalling(Request request) throws Exception {
        return objSerializableToByteArray(request);
    }

    @Override
    public Response unmarshalling(byte[] data) throws Exception {
        return (Response) byteArrayToObjSerializable(data);
    }

    //反序列化，将字节转化成对象，Object必须实现Serializable接口
    private static Object byteArrayToObjSerializable(byte[] bytes) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }

    //序列化，将对象转化成字节，Object必须实现Serializable接口
    private static byte[] objSerializableToByteArray(Object objSerializable) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(objSerializable);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return bytes;
    }

}