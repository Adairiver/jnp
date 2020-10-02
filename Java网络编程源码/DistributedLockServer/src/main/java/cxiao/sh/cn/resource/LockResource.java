package cxiao.sh.cn.resource;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @program: Java网络编程源码
 * @description:
 * @author: Xiao Chuan
 * @create: 2020/08/27 09:17
 */
@Component
@Path("lock")
public class LockResource implements Runnable{
    private ExecutorService fixPool = Executors.newCachedThreadPool();
    private ConcurrentHashMap<String, EventOutput> outputMap = new ConcurrentHashMap<>();
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_JSON_TYPE};
    private static MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    CyclicBarrier cb = new CyclicBarrier(2);
    public LockResource(){
        fixPool.submit(this);
        fixPool.shutdown();
    }
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Path("{uuid}")
    public EventOutput register(@PathParam("uuid") String uuid){
        System.out.println("client " + uuid + " 试图申请锁...");
        if (!outputMap.containsKey(uuid)){
            EventOutput output = new EventOutput();
            outputMap.put(uuid, output);
        }
        return outputMap.get(uuid);
    }
    @POST
    @Path("{uuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void postStudent(String uid, @PathParam("uuid") String uuid) throws Exception {
        System.out.println("client " + uuid + " 释放锁！");
        //从 Map中删除此uuid项，并关闭。
        if (outputMap.containsKey(uuid)){
            EventOutput output = outputMap.remove(uuid);
            output.close();
        }
        cb.await();
    }
    @Override
    public void run() { //单一线程
        while(true){
            // 从现有的申请者中随机的挑选一个，为其发放锁
            List<String> keyList = new ArrayList<String>(outputMap.keySet());
            if (keyList.size()>0) {
                String randKey = keyList.get(new Random().nextInt(keyList.size()));
                EventOutput output = outputMap.get(randKey);
                OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder().mediaType(randomMediaType());
                OutboundEvent event = eventBuilder.id(randKey)
                        .name(new Date().toString())
                        .data(String.class, randKey).build();
                try {
                    //精准推送给客户端，让其获得锁。避免惊群效应
                    output.write(event);
                    System.out.println("client " + randKey + " 获得了锁！");
                    //通过设置await(.)的超时参数可以消除获得锁的客户端死机造成的死锁
                    cb.await();  //发放了一个锁就停
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // 如果设置了上面的cb.await(.)的超时参数，则需要增加以下处理：
                    if (ex instanceof TimeoutException){
                        if (outputMap.containsKey(randKey)){
                            EventOutput op = outputMap.remove(randKey);
                            try {
                                op.close();
                            }catch (IOException ioe){}
                        }
                    }
                }
            }
        }
    }
}