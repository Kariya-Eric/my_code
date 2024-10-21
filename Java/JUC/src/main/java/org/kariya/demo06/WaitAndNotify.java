package org.kariya.demo06;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/18 16:56
 */
@Slf4j
public class WaitAndNotify {
    
    public static void main(String[] args) throws InterruptedException {
        demo04();
    }
    
    private static Object LOCK = new Object();
    
    public static void demo01() throws InterruptedException {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("执行...");
                try {
                    LOCK.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("其他代码...");
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("执行...");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("其他代码...");
            }
        }, "t2").start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.debug("唤醒其他线程");
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }
    
    public static void demo02() throws InterruptedException {
        new Thread(() -> {
            log.info("获得锁");
            synchronized (LOCK) {
                try {
                    //TimeUnit.SECONDS.sleep(1);
                    LOCK.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        TimeUnit.MILLISECONDS.sleep(500);
        synchronized (LOCK) {
            log.info("获得锁");
        }
    }
    
    //保护性暂停模式
    public static void demo03() throws InterruptedException {
        GuardedObject<String> go = new GuardedObject<>();
        new Thread(() -> {
            log.info("获取结果 ---> {}", go.get());
        }, "t1").start();
        new Thread(() -> {
            log.info("开始写入...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            go.set("Hello World");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("继续执行后续代码。。");
        }, "t2").start();
    }
    
    static int x = 0;
    
    public static void demo04() {
        Thread t2 = new Thread(() -> {
            log.info("变量修改...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            x = 10;
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t2");
        new Thread(() -> {
            try {
                t2.join();
                log.info("获取到值 ----> {}", x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1").start();
        t2.start();
    }
}

@Data
class GuardedObject<T> {
    private T t;
    
    public T get() {
        synchronized (this) {
            while (t == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return t;
        }
    }
    
    public void set(T t) {
        synchronized (this) {
            this.t = t;
            this.notifyAll();
        }
    }
}
