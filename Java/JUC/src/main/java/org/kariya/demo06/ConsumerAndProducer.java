package org.kariya.demo06;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/21 14:52
 */
public class ConsumerAndProducer {
    public static void main(String[] args) {
        Vector<Entity> vectors = new Vector();
        new Producer(vectors).start();
        new Producer(vectors).start();
        Consumer consumer = new Consumer(vectors);
        consumer.setPriority(Thread.MAX_PRIORITY);
        consumer.start();
    }
}

@Data
@AllArgsConstructor
class Entity {
    private int id;
    private int value;
}

@Slf4j
@AllArgsConstructor
class Producer extends Thread {
    private Vector<Entity> list;
    
    @Override
    public void run() {
        synchronized (list) {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                    while (list.size() > 20) {
                        log.info("请先消费部分产品后再生产...");
                        list.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Entity entity = new Entity(list.size(), (int) (Math.random() * 100));
                log.info("{} 生产产品 ---> {}", Thread.currentThread().getName(), entity);
                list.add(entity);
                list.notifyAll();
            }
        }
    }
}

@Slf4j
@AllArgsConstructor
class Consumer extends Thread {
    private Vector<Entity> list;
    
    @Override
    public void run() {
        synchronized (list) {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                    if (list.size() == 0) {
                        log.info("请先生成部分产品后再消费...");
                        list.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("消费产品 ---> {}", list.remove(0));
                list.notifyAll();
            }
        }
    }
}