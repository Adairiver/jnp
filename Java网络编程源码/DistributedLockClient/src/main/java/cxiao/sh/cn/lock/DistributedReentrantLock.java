package cxiao.sh.cn.lock;

/**
 * @program: Java网络编程源码
 * @description:
 * @author: Xiao Chuan
 * @create: 2020/08/28 17:30
 */
public class DistributedReentrantLock extends DistributedLock {
    private ThreadLocal<Integer> reentrantCount = new ThreadLocal<>();
    public DistributedReentrantLock(String sURL){
        super(sURL);
        reentrantCount.set(null);
    }
    @Override
    public void lock() {
        //可重入处理
        if (this.reentrantCount.get()!=null){
            int count = this.reentrantCount.get();
            if (count > 0){
                this.reentrantCount.set(++count);
                return;
            }
        }
        super.lock();
        reentrantCount.set(1);
    }
    @Override
    public void unlock() {
        //重入时的退出处理
        if (this.reentrantCount.get()!=null){
            int count = this.reentrantCount.get();
            if (count > 1){
                this.reentrantCount.set(--count);
                return;
            }else{
                this.reentrantCount.set(null);
            }
        }
        super.unlock();
    }
}
