package cxiao.sh.cn;

import cxiao.sh.cn.handler.EchoHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@SpringBootApplication
@EnableWebSocket
public class MyApplication implements WebSocketConfigurer {
    //启动后，在浏览器输入 http://localhost:8080
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
    @Autowired
    EchoHandler echoHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(echoHandler, "/echo");
    }
}