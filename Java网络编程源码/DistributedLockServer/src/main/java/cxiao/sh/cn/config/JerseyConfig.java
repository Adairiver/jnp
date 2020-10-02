package cxiao.sh.cn.config;

import cxiao.sh.cn.resource.LockResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * @program: Java网络编程源码
 * @description:
 * @author: Xiao Chuan
 * @create: 2020/08/23 20:46
 */
@Component
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(){
        register(LockResource.class);
        register(MoxyXmlFeature.class);
        register(JacksonFeature.class);
        register(SseFeature.class);
    }
}