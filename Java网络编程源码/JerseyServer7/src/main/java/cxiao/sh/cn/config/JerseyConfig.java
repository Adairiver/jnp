package cxiao.sh.cn.config;

import cxiao.sh.cn.resource.SseBroadcastResource;
import cxiao.sh.cn.resource.SseSubPubResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Component
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(){
        register(SseSubPubResource.class);
        register(SseBroadcastResource.class);
        register(MoxyXmlFeature.class);
        register(JacksonFeature.class);
        register(SseFeature.class);
    }
}