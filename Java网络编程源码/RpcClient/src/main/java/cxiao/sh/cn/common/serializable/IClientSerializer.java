package cxiao.sh.cn.common.serializable;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public interface IClientSerializer<M, U> {
    //把第一个泛型M序列化，把第二个泛型U反序列化
    byte[] marshalling(M t) throws Exception;
    U unmarshalling(byte[] data) throws Exception;
}