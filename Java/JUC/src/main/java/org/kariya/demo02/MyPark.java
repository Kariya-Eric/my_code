package org.kariya.demo02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/16 11:08
 */

/*
 * park线程
 * */
@Slf4j
public class MyPark {
    public static void main(String[] args) throws InterruptedException {
        demo02();
    }
    
    public static void demo01() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("park...");
            LockSupport.park();
            log.info("unpark...");
            log.info("打断状态");
            Thread.interrupted();
            //重置打断标记
            LockSupport.park();
            log.info("unpark...");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        t1.interrupt();
    }
    
    public static void demo02() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }
}
