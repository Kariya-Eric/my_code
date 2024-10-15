package org.kariya.demo02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/15 15:10
 */
@Slf4j
public class MyInterrupted {
    
    public static void main(String[] args) throws InterruptedException {
        demo02();
    }
    
    public static void demo01() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("sleeping...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("interrupted");
        t1.interrupt();
        log.info("打断标记 -----> {}", t1.isInterrupted());
    }
    
    public static void demo02() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            int count = 0;
            while (true) {
                boolean interrupted = Thread.currentThread().isInterrupted();
                log.info("-----> {} {}", count++, interrupted);
                if (interrupted) {
                    log.info("interrupted,exit");
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.debug("interrupt");
        t1.interrupt();
    }
}
