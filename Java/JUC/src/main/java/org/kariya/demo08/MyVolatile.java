package org.kariya.demo08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/23 13:52
 */
@Slf4j
public class MyVolatile {
    public static void main(String[] args) throws InterruptedException {
        demo04();
    }
    
    volatile static boolean flag = true;
    
    //volatile关键字并不能保证原子性
    public static void demo01() throws InterruptedException {
        new Thread(() -> {
            int count = 0;
            while (flag) {
                log.info("print... ---> {} ", count++);
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("exit...");
        }).start();
        TimeUnit.SECONDS.sleep(2);
        flag = false;
    }
    
    private static Object lock = new Object();
    
    private static boolean f = true;
    
    public static void demo02() throws InterruptedException {
        new Thread(() -> {
            int count = 0;
            while (true) {
                synchronized (lock) {
                    if (f) {
                        log.info("print... ---> {} ", count++);
                        try {
                            TimeUnit.MILLISECONDS.sleep(300);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        log.info("exit...");
                        break;
                    }
                }
            }
        }).start();
        TimeUnit.SECONDS.sleep(2);
        synchronized (lock) {
            f = false;
        }
    }
    
    //使用打断标记完成线程的优雅退出
    public static void demo03() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("被打断..准备退出");
                    return;
                }
                try {
                    log.info("---> {}", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(5);
        t1.interrupt();
    }
    
    private volatile static boolean monitor = true;
    
    public static void demo04() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (!monitor) {
                    log.info("被打断..准备退出");
                    return;
                }
                log.info("---> {}", Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        TimeUnit.SECONDS.sleep(5);
        monitor = false;
    }
    
}
