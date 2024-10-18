package org.kariya.demo05;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/18 10:29
 */
@Slf4j
public class Lock {
    
    public static void main(String[] args) throws InterruptedException {
        demo03();
    }
    
    //可偏向锁在加锁后会保留当前线程信息
    public static void demo01() throws InterruptedException {
        Dog dog = new Dog();
        log.info("before lock ---- \n {}", ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            log.info("locking --- \n {}", ClassLayout.parseInstance(dog).toPrintable());
        }
        log.info("after lock ---- \n {}", ClassLayout.parseInstance(dog).toPrintable());
    }
    
    //hashcode后可偏向锁会无法写入线程id，写入hashcode，自动膨胀为轻量锁
    public static void demo02() throws InterruptedException {
        Dog dog = new Dog();
        //00001101 00000000 00000000 00000000 可偏向
        log.info("before lock ---- \n {}", ClassLayout.parseInstance(dog).toPrintable());
        dog.hashCode();
        synchronized (dog) {
            //00001000 11110011 10001111 10010101 轻量级锁
            log.info("locking --- \n {}", ClassLayout.parseInstance(dog).toPrintable());
        }
        log.info("after lock ---- \n {}", ClassLayout.parseInstance(dog).toPrintable());
    }
    
    public static void demo03() throws InterruptedException {
        List<Dog> dogs = new ArrayList<>();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Dog d = new Dog();
                synchronized (d) {
                    dogs.add(d);
                }
            }
            try {
                //保证线程存活
                TimeUnit.SECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1").start();
        TimeUnit.SECONDS.sleep(2);
        log.info("list中对象的MarkWord --- \n , {}", ClassLayout.parseInstance(dogs.get(19)).toPrintable());
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog dog = dogs.get(i);
                synchronized (dog) {
                    if (i == 17 || i == 18 || i == 19) {
                        log.info("第{}次偏向结果--- \n , {}", i + 1, ClassLayout.parseInstance(dog).toPrintable());
                    }
                }
            }
            try {
                //保证线程存活
                TimeUnit.SECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t2").start();
        TimeUnit.SECONDS.sleep(2);
        log.info("list中第11个对象的MarkWord --- \n , {}", ClassLayout.parseInstance(dogs.get(10)).toPrintable());
        log.info("list中第26个对象的MarkWord --- \n , {}", ClassLayout.parseInstance(dogs.get(25)).toPrintable());
        log.info("list中第41个对象的MarkWord --- \n , {}", ClassLayout.parseInstance(dogs.get(40)).toPrintable());
    }
}

class Dog {
}
