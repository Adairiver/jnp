package cxiao.sh.cn;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        System.out.println("注册 Jersey Servlet");
        //第一个参数指明使用Jersey自带的Servlet，
        //第二个参数指定了Jersey Resource的根路径，等同于 @ApplicationPath标注
        //第二个参数的*不能少！
        ServletRegistrationBean servletRegistrationBean =
                new ServletRegistrationBean(new ServletContainer(), "/jersey/*");
        Map<String, String> map = new HashMap<>();

        //指定Jersey的配置类，第一个参数是Jersey约定的，不用变
        map.put("javax.ws.rs.Application", "cxiao.sh.cn.config.JerseyConfig");
        //指定Jersey Resource包扫描路径，第一个参数是Jersey约定的，不用变。
        // 可以与上一句同时使用
        //map.put("jersey.config.server.provider.packages", "cxiao.sh.cn.another");
        servletRegistrationBean.setInitParameters(map);
        return servletRegistrationBean;
    }
}