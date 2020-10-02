package cxiao.sh.cn.config;

import cxiao.sh.cn.resource.FileResource;
import cxiao.sh.cn.resource.StudentResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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
        register(StudentResource.class);
        register(FileResource.class);

        register(MoxyXmlFeature.class);
        register(JacksonFeature.class);
        register(MultiPartFeature.class);
    }
}