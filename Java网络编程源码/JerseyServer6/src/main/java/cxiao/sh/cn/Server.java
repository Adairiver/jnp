package cxiao.sh.cn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@SpringBootApplication
public class Server {
    public static final ExecutorService pool = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}