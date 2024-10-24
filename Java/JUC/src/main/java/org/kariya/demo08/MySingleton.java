package org.kariya.demo08;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/24 10:57
 */
@Slf4j
public class MySingleton {
    private MySingleton() {
    }
    
    private static volatile MySingleton instance = null;
    
    //double checking
    public static MySingleton getInstance() {
        //可能会出现指令重排
        if (instance == null) {
            synchronized (MySingleton.class) {
                if (instance == null) {
                    instance = new MySingleton();
                }
                return instance;
            }
        }
        return instance;
        
    }
    
    public static void main(String[] args) {
        demo02();
    }
    
    //happens-before
    static int x;
    static Object m = new Object();
    
    //线程锁之后的写对另一个线程读可见
    public static void demo01() {
        new Thread(() -> {
            synchronized (m) {
                x = 10;
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (m) {
                log.info(" ----> {}", x);
            }
        }, "t2").start();
    }
    
    //volatile变量的写，对其他接下里的线程的读可见
    private static volatile int y;
    
    public static void demo02() {
        y = 10;
        new Thread(() -> {
            log.info(" ----> {}", y);
        }).start();
    }
    
    //线程结束(被打断)前对变量的写，对其他线程得知他结束后的读可见
    public static void demo03() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            x = 10;
        });
        t1.start();
        t1.join();
        log.info(" ----> {}", y);
    }
    
    public static void demo04() {
        new Thread(() -> {
            x = 10;
            y = 20; //y之后存在写屏障 写屏障之前
        }, "t1").start();
        new Thread(() -> {
            log.info(" ---> {} ", y); //之前有读屏障
        }, "t2").start();
    }
}
