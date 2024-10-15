package org.kariya.demo02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 。
 *
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/15 10:28
 */
@Slf4j
public class MyThread extends Thread {
    
    public MyThread() {
    }
    
    public MyThread(String name) {
        super(name);
    }
    
    @Override
    public void run() {
        //要执行的任务
    }
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        demo05();
    }
    
    public static void demo01() throws ExecutionException, InterruptedException {
        //创建线程的第一种方法
        Thread t1 = new MyThread("t1");
        t1.setName("t1");
        Thread t2 = new Thread(() -> {
            //Runnable接口的实现
        }, "t2");
        //futuretask创建线程第三种方式
        FutureTask<String> ft = new FutureTask<>(() -> "FutureTask返回结果");
        Thread t3 = new Thread(ft);
        String result = ft.get();
    }
    
    public static void demo02() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.info("wake up...");
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();
        log.info("t1 state,{}", t1.getState());
        Thread.sleep(500);
        log.info("interrupt");
        t1.interrupt();
        log.info("t1 state,{}", t1.getState());
    }
    
    public static void demo03() {
        new Thread(() -> {
            int count = 0;
            while (true) {
                log.info("{} -----> {}", Thread.currentThread().getName(), count++);
            }
        }, "t1").start();
        new Thread(() -> {
            int count = 0;
            while (true) {
                Thread.yield();
                log.info("{} -----> {}", Thread.currentThread().getName(), count++);
            }
        }, "t2").start();
    }
    
    private static int r = 0;
    
    public static void demo04() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            r = 10;
        }, "t1");
        t1.start();
        t1.join();
        log.debug("结果 ----> {}", r);
        log.debug("结束");
    }
    
    private static int r1 = 0;
    private static int r2 = 0;
    
    public static void demo05() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            r1 = 10;
        }, "t1");
        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            r2 = 20;
        }, "t2");
        
        t1.start();
        t2.start();
        long start = System.currentTimeMillis();
        log.info("join begin");
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        log.debug("结果 ----> {},{} 耗时 ----> {}", r1, r2, end - start);
    }
}
