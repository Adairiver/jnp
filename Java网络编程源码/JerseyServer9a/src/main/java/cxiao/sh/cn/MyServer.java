package cxiao.sh.cn;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class MyServer {
    public static void main(String[] args) {
        MyServer myServer = new MyServer();
        myServer.service();
    }
    public void service(){
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
         //配置Servlet, 用代码配置，不用web.xml
        ServletHolder holder = context.addServlet(ServletContainer.class, "/jersey/*");
        holder.setInitOrder(1);
        //.setInitParameter可以多次调用
        // 第一个参数值是固定不变的，类似于 @ApplicationPath 标注。
        // 因为Server是自建的，所以直接使用@ApplicationPath 无效
        holder.setInitParameter("javax.ws.rs.Application", "cxiao.sh.cn.config.JerseyConfig");
        // 第一个参数值也是固定不变的，用来指明包扫描路径
        //holder.setInitParameter("jersey.config.server.provider.packages", "cxiao.sh.cn.another");
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}