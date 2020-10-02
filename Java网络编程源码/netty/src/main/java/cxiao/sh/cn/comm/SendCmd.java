package cxiao.sh.cn.comm;

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
public class SendCmd {
    private String filePath;
    private boolean closeAfterSending;
    public SendCmd(String filePath){
        this.filePath = filePath;
        closeAfterSending = false;
    }
}