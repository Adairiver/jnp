package cxiao.sh.cn;

import cxiao.sh.cn.entity.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client {
    //候选数据格式列表
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};
    //候选主题列表
    private final static String[] topics = new String[]{"hello", "world", "shanghai"};
    private static AtomicInteger idCounter = new AtomicInteger(1000);
    private EventSource eventSource;
    private String topicName;
    private int cid;
    private static MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    private static String randomTopicName() {return topics[new Random().nextInt(topics.length)];}
    private WebTarget target;
    public static void main(String[] args) {
        System.out.println("测试服务端 SseSubPubResource");
        test("http://localhost:8080/jersey/chat");
        System.out.println("测试服务端 SseBroadcastResource");
        test("http://localhost:8080/jersey/cast");
        try { //防止主线程退出,因为要每个客户端需要保持长连接才能接收到事件
            Thread.currentThread().join();
        } catch (InterruptedException e) { }
    }
    public static void test(String url){
        List<Client> clients = new ArrayList<>();
        //创建多个客户端，每个客户端都注册
        for (int i=0;i<10;i++) {
            clients.add(new Client(url));
        }
        int postTimes = 6;
        for (int i=0; i<postTimes; i++) {
            //随机选取一个客户端作为发布者
            Client client = clients.get(new Random().nextInt(clients.size()));
            client.postEvent();
        }
    }
    public Client(String url){
        //随机指定一个主题
        topicName = randomTopicName();
        //设置client id，便于追踪
        cid = idCounter.incrementAndGet();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MoxyXmlFeature.class);
        clientConfig.register(JacksonFeature.class);
        clientConfig.register(SseFeature.class);
        javax.ws.rs.client.Client rsClient = ClientBuilder.newClient(clientConfig);
        target = rsClient.target(url);
        eventSource = new EventSource(target.path(topicName)){
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                try{
                    Student stu = inboundEvent.readData(Student.class);
                    System.out.println("\t\t主题" + topicName + "的订阅者client" + cid + " 收到推送，主题：" + inboundEvent.getName() + "，学号：" + stu.getId());
                    //inboundEvent.getId();inboundEvent.getName();inboundEvent.getComment();inboundEvent.getRawData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        System.out.println("client" + cid + " 订阅了主题 " + topicName);
    }
    public void postEvent(){
        Student student = StuRepository.getStudent(new Random().nextInt(5) + 101);
        MediaType mediaType = randomMediaType();
        Entity<Student> studentEntity = Entity.entity(student, mediaType);
        mediaType = randomMediaType();
        //随意选择一个主题，并提交
        String topic = randomTopicName();
        System.out.println("client" + cid + " 提交，主题：" + topic + ",学号：" + student.getId());
        target.path(topic)
                .request(mediaType)
                .post(studentEntity);
    }
    //这里没用上
    private void closeEvent(){
        eventSource.close();
    }
}