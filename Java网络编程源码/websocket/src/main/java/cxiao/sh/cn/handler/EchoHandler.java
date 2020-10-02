package cxiao.sh.cn.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Component
public class EchoHandler extends TextWebSocketHandler {
    public static final ConcurrentLinkedQueue<WebSocketSession> clients = new ConcurrentLinkedQueue<WebSocketSession>();
    private static final DateTimeFormatter sf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 产生随机名称，只发送给当前连接者
        String randName = getRandomString(8);
        session.sendMessage(new TextMessage(randName + " 建立连接！"));
        // 保存名称，以便群发时使用
        Map<String, Object> map = session.getAttributes();
        map.put("name", randName);
        // 添加至连接者列表
        clients.add(session);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 当服务端接收到任一连接者提交的消息时
        var msg = message.getPayload();
        // 稍作加工，转发给全部的连接者。转发信息包含提交者的随机名称
        for (WebSocketSession client:clients){
            String strMsg = LocalDateTime.now().format(sf) ;
            client.sendMessage(new TextMessage(strMsg));
            strMsg = session.getAttributes().get("name") +  " : " + msg;
            client.sendMessage(new TextMessage(strMsg));
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        clients.remove(session);
    }

    //生成指定length的随机字符串（A-Z，a-z，0-9）
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}