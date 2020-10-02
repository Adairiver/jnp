package cxiao.sh.cn.register;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
public class ServiceObject {
    // interf 和 version 构成联合主键
    private Class<?> interf;
    private String verseion;
    private Object obj;
}