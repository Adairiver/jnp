package cxiao.sh.cn.comm;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Sender {
    //发送包含字节个数的一组字节。
    private static void send(Socket socket, byte[] obytes) throws Exception{
        OutputStream outs = socket.getOutputStream();
        int bytesCount = obytes.length;
        //先发送字节数
        byte[] cntBytes = Utils.intToByteArray(bytesCount);
        outs.write(cntBytes, 0, cntBytes.length);
        outs.flush();
        outs.write(obytes, 0, obytes.length);
        outs.flush();
    }

    //send(.)的另一个版本，采用ObjectOutputStream
    private static void send0(Socket socket, byte[] obytes) throws Exception{
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeInt(obytes.length);
        oos.write(obytes);
        oos.flush();
    }

    //发送一个字符串
    public static void sendString(Socket socket, String msg) throws Exception{
        byte[] obytes = msg.getBytes("utf-8");
        send(socket, obytes);
    }

    //发送一个对象，该对象必须实现Serializable接口
    public static void sendObject(Socket socket, Object objSerializable) throws Exception{
        byte[] bytes = Utils.objSerializableToByteArray(objSerializable);
        Sender.send(socket, bytes);
    }

    //sendObject(.)的另一个版本，采用ObjectOutputStream
    public static void sendObject0(Socket socket, Object objSerializable) throws Exception {
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(objSerializable);
        oos.flush();
    }

    //sendObject(.)的JSON版本。
    // Object->Json字符串，再以字符串的方式发送。
    // 不要求参数object实现Serializable接口
    public static void sendObjectByJson(Socket socket, Object object) throws Exception{
        String msg = JSON.toJSONString(object);
        sendString(socket, msg);
    }

    //发送一个文件，支持大文件
    public static void sendFile(Socket socket, String localFileName) throws Exception{
        OutputStream os = socket.getOutputStream();
        File file = new File(localFileName);
        long fileLength = file.length();
        //发送文件总的字节数,java的long是8个字节
        byte[] lengthBytes = Utils.longToByteArray(fileLength);
        os.write(lengthBytes);
        os.flush();
        FileInputStream fis = new FileInputStream(file);
        //缓冲区设为1000*1000个字节
        int bufSize = 1000*1000;
        byte[] buffer = new byte[bufSize];
        //从文件读取一组字节
        int readLength = fis.read(buffer);
        //未到文件末尾
        while (readLength != -1){
            //发送一组字节
            os.write(buffer, 0, readLength);
            os.flush();
            //从文件读取下一组字节
            readLength = fis.read(buffer);
        }
        fis.close();
    }
}