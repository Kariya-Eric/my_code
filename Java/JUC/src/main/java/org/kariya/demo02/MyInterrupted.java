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
        demo03();
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
    
    //两阶段中止模式,t1线程优雅中止t2线程
    public static void demo03() throws InterruptedException {
        //均不推荐
        //thread stop 停止线程
        //System.exit 中止进程
        Thread monitor = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("一些善后工作");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                    log.info("monitor ------> ");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }, "monitor");
        monitor.start();
        TimeUnit.SECONDS.sleep(6);
        log.info("主线程休眠5s后中止monitor");
        monitor.interrupt();
    }
    
}
