package cxiao.sh.cn;

import cxiao.sh.cn.client.SimpleMail;
import cxiao.sh.cn.client.MailUtils;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class MailClient {
    public static void main(String[] args) {
        SimpleMail simpleMail = new SimpleMail();
        simpleMail.setHost("smtp.qq.com");
        simpleMail.setPort("25");
        simpleMail.setUsername("184088321");
        simpleMail.setPassword("buzzrzrfjdhmcahd");
        simpleMail.setFrom("184088321@qq.com");
        simpleMail.setTo(new String[]{"184088321@qq.com","cxiao@fudan.edu.cn"});
        simpleMail.setCc(null);
        simpleMail.setBcc(null);
        simpleMail.setSubject("Test Mail");
        simpleMail.setBody("Hello! I am xiaochuan from qq.");
        simpleMail.setAttachment(new String[]{"D:\\ClientFiles\\abc.txt","D:\\ServerFiles\\xyz.pdf"});
        simpleMail.setIsHtml(false);
        MailUtils.sendMail(simpleMail, 3);
    }
}

