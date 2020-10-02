package cxiao.sh.cn.client;

import lombok.Data;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
public class SimpleMail {
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String from;
    private String host;
    private String port;
    private String subject;
    private String body;
    private String[] attachment;
    private String username;
    private String password;
    private Boolean isHtml;
}