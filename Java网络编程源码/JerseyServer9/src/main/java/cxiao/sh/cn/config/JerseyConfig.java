package cxiao.sh.cn.config;

import com.alibaba.fastjson.support.jaxrs.FastJsonFeature;
import cxiao.sh.cn.filter.UserAuthFilter;
import cxiao.sh.cn.resource.FileResource;
import cxiao.sh.cn.resource.StudentResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

//在ServletRegistrationBean中配置ApplicationPath
//@Component
//@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(){
        register(StudentResource.class);
        register(FileResource.class);

        register(UserAuthFilter.class);
        register(RolesAllowedDynamicFeature.class);


        register(MoxyXmlFeature.class);
        register(FastJsonFeature.class);
        //register(JacksonFeature.class); 被FastJsonFeature代替
        register(MultiPartFeature.class);
    }
}