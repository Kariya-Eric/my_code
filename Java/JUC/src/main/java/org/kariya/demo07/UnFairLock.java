package org.kariya.demo07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 15:42
 */
@Slf4j
public class UnFairLock {
    //不公平锁
    private static ReentrantLock lock = new ReentrantLock(false);
    
    public static void main(String[] args) throws InterruptedException {
        demo01();
    }
    
    static int t1 = 0;
    static int t2 = 0;
    
    public static void demo01() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    t1++;
                } finally {
                    lock.unlock();
                }
            }
        }, "t1").start();
        new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    t2++;
                } finally {
                    lock.unlock();
                }
            }
        }, "t2").start();
        TimeUnit.SECONDS.sleep(5);
        log.info("t1 ---> {} ; t2 ---> {}", t1, t2);
    }
}
