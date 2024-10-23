package org.kariya.demo07;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 11:08
 */
@Slf4j
public class DeadLock {
    public static void main(String[] args) {
        demo03();
    }
    
    public static void demo01() {
        Object a = new Object();
        Object b = new Object();
        new Thread(() -> {
            synchronized (a) {
                log.info("获得a对象锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("尝试获得b对象锁");
                synchronized (b) {
                    log.info("执行b");
                }
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (b) {
                log.info("获得b对象锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("尝试获得a对象锁");
                synchronized (a) {
                    log.info("执行a");
                }
            }
        }, "t2").start();
    }
    
    public static void demo02() {
        ChopStick c1 = new ChopStick("1");
        ChopStick c2 = new ChopStick("2");
        ChopStick c3 = new ChopStick("3");
        ChopStick c4 = new ChopStick("4");
        ChopStick c5 = new ChopStick("5");
        new Eater("苏格拉底", c1, c2).start();
        new Eater("亚里士多德", c2, c3).start();
        new Eater("柏拉图", c3, c4).start();
        new Eater("阿基米德", c4, c5).start();
        new Eater("尼采", c5, c1).start();
    }
    
    static int count = 10;
    
    //活锁
    public static void demo03() {
        new Thread(() -> {
            while (count > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    count--;
                    log.debug("count ----> {}", count);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            while (count < 20) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    count++;
                    log.debug("count ----> {}", count);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2").start();
    }
}

@Data
@AllArgsConstructor
class ChopStick {
    private String name;
}

@Slf4j
@AllArgsConstructor
class Eater extends Thread {
    private String name;
    private ChopStick left;
    private ChopStick right;
    
    @Override
    public void run() {
        while (true) {
            synchronized (left) {
                synchronized (right) {
                    log.info("用户 {} 开始吃饭...", this.name);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}