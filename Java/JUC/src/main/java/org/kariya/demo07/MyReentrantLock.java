package org.kariya.demo07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 13:46
 */
@Slf4j
public class MyReentrantLock {
    static ReentrantLock lock = new ReentrantLock();
    
    public static void main(String[] args) throws InterruptedException {
        demo03();
    }
    
    public static void demo01() {
        lock.lock();
        try {
            log.info("demo01");
            method01();
        } finally {
            lock.unlock();
        }
    }
    
    //可打断锁
    public static void demo02() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("try lock...");
                lock.lockInterruptibly();
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    log.info("locked...");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "t1");
        lock.lock();
        t1.start();
        TimeUnit.SECONDS.sleep(2);
        log.info("lock interrupt");
        //lock.unlock();
        t1.interrupt();
    }
    
    //锁超时
    public static void demo03() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("尝试获得锁...");
            try {
                if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                    log.info("获取不到锁...");
                    return;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                log.info("获得到锁...");
            } finally {
                lock.unlock();
            }
        }, "t1");
        lock.lock();
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        lock.unlock();
    }
    
    private static void method01() {
        lock.lock();
        try {
            log.info("m1");
            method02();
        } finally {
            lock.unlock();
        }
    }
    
    private static void method02() {
        lock.lock();
        try {
            log.info("m2");
        } finally {
            lock.unlock();
        }
    }
}