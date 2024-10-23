package org.kariya.demo07;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 15:52
 */
@Slf4j
public class MyCondition {
    
    private static ReentrantLock lock = new ReentrantLock();
    
    public static void main(String[] args) throws InterruptedException {
        demo01();
    }
    
    public static void demo01() throws InterruptedException {
        MyCount count = new MyCount();
        new Producer("生产者", count).start();
        new Consumer("消费者", count).start();
    }
    
}

@Data
class MyCount {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
}

@AllArgsConstructor
@Slf4j
class Consumer extends Thread {
    private String name;
    private MyCount count;
    
    @Override
    public void run() {
        while (true) {
            count.getLock().lock();
            while (count.getCount() == 0) {
                log.info("{},count ---> {} 请先生产...", this.name, count.getCount());
                try {
                    count.getCondition().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                count.setCount(count.getCount() - 1);
                log.info("{} 消费产品后,count ---> {}", this.name, count.getCount());
                TimeUnit.SECONDS.sleep(1);
                count.getCondition().signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                count.getLock().unlock();
            }
        }
    }
}

@AllArgsConstructor
@Slf4j
class Producer extends Thread {
    private String name;
    private MyCount count;
    
    @Override
    public void run() {
        while (true) {
            count.getLock().lock();
            while (count.getCount() == 20) {
                log.info("{},count ---> {} 请先消费...", this.name, count.getCount());
                try {
                    count.getCondition().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                count.setCount(count.getCount() + 1);
                log.info("{} 生产产品后,count ---> {}", this.name, count.getCount());
                TimeUnit.SECONDS.sleep(1);
                count.getCondition().signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                count.getLock().unlock();
            }
        }
    }
}