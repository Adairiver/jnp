package cxiao.sh.cn.lock;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @program: Java网络编程源码
 * @description:
 * @author: Xiao Chuan
 * @create: 2020/08/28 17:30
 */
public class DistributedLock implements Lock {
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_JSON_TYPE};
    private static MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    private String sURL;
    private EventSource eventSource;
    private String uuid = null;
    private WebTarget target;
    public DistributedLock(String sURL){
        this.sURL = sURL;
        if (this.sURL==null) {
            this.sURL = "http://localhost:8080/jersey/lock";
        }
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MoxyXmlFeature.class);
        clientConfig.register(JacksonFeature.class);
        clientConfig.register(SseFeature.class);
        javax.ws.rs.client.Client rsClient = ClientBuilder.newClient(clientConfig);
        target = rsClient.target(this.sURL);
    }
    @Override
    public void lock() { //阻塞直至得到锁为止
        //每一次lock(.)都是重新订阅
        CountDownLatch latch = new CountDownLatch(1);
        if (uuid==null){
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
        }
        eventSource = new EventSource(target.path(uuid)){
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                try{
                    //返回的是uuid
                    String msg = inboundEvent.readData(String.class);
                    if (msg.equals(uuid)){
                        latch.countDown();
                        System.out.println("client " + uuid + " 获得锁！");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        System.out.println("client " + uuid + " 申请锁...");
        try {
            latch.await();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {
    }
    @Override
    public boolean tryLock() {
        return false;
    }
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }
    @Override
    public void unlock() {
        if (uuid==null){
            return;
        }
        MediaType mediaType = randomMediaType();
        Entity<String> msgEntity = Entity.entity(uuid, mediaType);
        mediaType = randomMediaType();
        target.path(uuid)
                .request(mediaType)
                .post(msgEntity);
        System.out.println("client " + uuid + " 释放了锁！");
        eventSource.close();
    }
    @Override
    public Condition newCondition() {
        return null;
    }
}