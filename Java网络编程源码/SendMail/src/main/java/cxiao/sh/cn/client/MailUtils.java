package cxiao.sh.cn.client;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class MailUtils {
    public static void sendMail(SimpleMail simpleMail, int tryTimes) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", simpleMail.getHost());
        properties.setProperty("mail.smtp.port", simpleMail.getPort());
        properties.put("mail.smtp.auth", "true");
        int i = 0;
        while(i<tryTimes) {
            try {
                Session session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(simpleMail.getUsername(), simpleMail.getPassword());
                    }
                });
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(simpleMail.getFrom()));
                List<InternetAddress> toList = new ArrayList<>();
                List<InternetAddress> ccList = new ArrayList<>();
                List<InternetAddress> bccList = new ArrayList<>();
                if (simpleMail.getTo() != null) {
                    for (String str : simpleMail.getTo()) {
                        toList.add(new InternetAddress(str));
                    }
                    message.addRecipients(Message.RecipientType.TO, toList.toArray(new InternetAddress[]{}));
                }

                if (simpleMail.getCc() != null) {
                    for (String str : simpleMail.getCc()) {
                        ccList.add(new InternetAddress(str));
                    }
                    message.addRecipients(Message.RecipientType.CC, ccList.toArray(new InternetAddress[]{}));
                }
                if (simpleMail.getBcc() != null) {
                    for (String str : simpleMail.getBcc()) {
                        bccList.add(new InternetAddress(str));
                    }
                    message.addRecipients(Message.RecipientType.BCC, bccList.toArray(new InternetAddress[]{}));
                }
                message.setSubject(simpleMail.getSubject());
                BodyPart messageBodyPart = new MimeBodyPart();
                Multipart multipart = new MimeMultipart();
                if (simpleMail.getIsHtml()) {
                    messageBodyPart.setContent(simpleMail.getBody(), "text/html;charset=utf-8");
                } else {
                    messageBodyPart.setContent(simpleMail.getBody(), "text/plain;charset=utf-8");
                }
                multipart.addBodyPart(messageBodyPart);
                if (simpleMail.getAttachment() != null) {
                    for (String str : simpleMail.getAttachment()) {
                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(str);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(MimeUtility.encodeText(str.substring(str.lastIndexOf("\\") + 1)));
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("发送成功!!!!!");
                break;
            }catch (MessagingException | UnsupportedEncodingException mex) {
                mex.printStackTrace();
                System.out.println("第" + (++i) + "发送失败:" + simpleMail.getTo()[0]);
            }
        }
    }
}