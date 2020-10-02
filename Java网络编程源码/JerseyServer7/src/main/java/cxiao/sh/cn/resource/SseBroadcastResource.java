package cxiao.sh.cn.resource;

import cxiao.sh.cn.entity.Student;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Component
@Path("cast")
public class SseBroadcastResource {
    //SseBroadcaster管理一组EventOutput
    private ConcurrentHashMap<String, SseBroadcaster> outputMap = new ConcurrentHashMap<>();
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Path("{topic}")
    public EventOutput register(@PathParam("topic") String topic){
        System.out.println("主题" + topic + "被注册...");
        EventOutput output = new EventOutput();
        if (!outputMap.containsKey(topic)){
            outputMap.put(topic, new SseBroadcaster());
        }
        outputMap.get(topic).add(output);
        return output;
    }
    @POST
    @Path("{topic}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void postStudent(Student stu, @PathParam("topic") String topic) throws Exception {
        System.out.println("主题" + topic + "被提交信息，学号" + stu.getId());
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder().mediaType(MediaType.APPLICATION_JSON_TYPE);
        OutboundEvent event = eventBuilder
                .id(System.nanoTime() + "")
                .name(topic)
                .data(Student.class, stu).build();
        if (outputMap.get(topic)==null){
            System.out.println("此主题" + topic + "尚未订阅，无法发布！");
            return;
        }
        outputMap.get(topic).broadcast(event);
    }
}