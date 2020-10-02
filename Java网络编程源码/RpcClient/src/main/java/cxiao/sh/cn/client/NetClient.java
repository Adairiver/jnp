package cxiao.sh.cn.client;

import cxiao.sh.cn.common.serializable.IClientSerializer;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public interface NetClient<T, U> {
    U sendRequest(T request, String sAddress, IClientSerializer<T, U> serializing) throws Throwable;
}
