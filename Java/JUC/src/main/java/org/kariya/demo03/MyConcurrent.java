package org.kariya.demo03;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/16 16:34
 */
@Slf4j
public class MyConcurrent {
    
    static int count = 0;
    
    public static void main(String[] args) throws InterruptedException {
        demo03();
    }
    
    public static void demo01() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count++;
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                count--;
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("count -----> {}", count);
    }
    
    public static void demo02() throws InterruptedException {
        Object obj = new Object();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (obj) {
                    count++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                for (int i = 0; i < 5000; i++) {
                    count--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("count -----> {}", count);
    }
    
    private static List list = new ArrayList<>();
    
    public static void demo03() {
        new Thread(() -> method(), "t1").start();
        new Thread(() -> method(), "t2").start();
    }
    
    private static void method() {
        for (int i = 0; i < 2000; i++) {
            method1();
            method2();
        }
    }
    
    private static void method1() {
        list.add("1");
    }
    
    private static void method2() {
        list.remove(0);
    }
}
