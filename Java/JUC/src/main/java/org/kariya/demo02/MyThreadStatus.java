package org.kariya.demo02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/16 15:33
 */
@Slf4j
public class MyThreadStatus {
    public static void main(String[] args) {
        demo01();
    }
    
    public static void demo01() {
        Thread t1 = new Thread(() -> {
        }, "t1");
        Thread t2 = new Thread(() -> {
            while (true) {
                //死循环
            }
        }, "t2");
        t2.start();
        Thread t3 = new Thread(() -> log.info("t3 running"), "t3");
        t3.start();
        Thread t4 = new Thread(() -> {
            synchronized (MyThread.class) {
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t4");
        t4.start();
        Thread t5 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t5");
        t5.start();
        Thread t6 = new Thread(() -> {
            synchronized (MyThread.class) {
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t6");
        t6.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug("t1 ---> {}", t1.getState().name());
        log.debug("t2 ---> {}", t2.getState().name());
        log.debug("t3 ---> {}", t3.getState().name());
        log.debug("t4 ---> {}", t4.getState().name());
        log.debug("t5 ---> {}", t5.getState().name());
        log.debug("t6 ---> {}", t6.getState().name());
    }
}
