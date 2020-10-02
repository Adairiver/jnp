package cxiao.sh.cn;

import cxiao.sh.cn.lock.DistributedReentrantLock;

import javax.ws.rs.core.MediaType;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Java网络编程源码
 * @description:
 * @author: Xiao Chuan
 * @create: 2020/08/28 19:59
 */
public class Client implements Runnable{
    // 此线程池用来专门运行当前Client的run()方法，所有Client共享静态变量number
    private final ExecutorService fixPool = Executors.newCachedThreadPool();
    private static int number = 0;
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_JSON_TYPE};
    private static MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    private CountDownLatch latch;
    public static void main(String[] args) {
        int concurrency = 200;
        CountDownLatch myLatch = new CountDownLatch(concurrency);
        for (int i=0;i<concurrency;i++) {
            new Client(myLatch);
            //模拟并发的场景
            myLatch.countDown();
            System.out.println((i+1) + " client 准备好了......");
        }
    }
    public Client(CountDownLatch latch) {
        this.latch = latch;
        fixPool.submit(this);
        // 在执行shutdown()后，已提交的任务会继续处理而不允许再提交新的任务
        // 所以 fixPool 不能是静态变量！
        fixPool.shutdown();
    }
    @Override
    public void run(){
        try {
            latch.await();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        //两个类一个是不可重入锁，另一个是可重入锁；
        //测试不可重入锁时要把嵌套的lock()删除。
        //初始化参数指明提供分布式锁服务的地址
        DistributedReentrantLock alock = new DistributedReentrantLock("http://localhost:8080/jersey/lock");
        alock.lock();
        number = number + 1;
        alock.lock(); //测试锁的可重入性
        System.out.println("目前的数值是:");
        System.out.println(number);
        alock.unlock();
        alock.unlock();
    }
}