package org.kariya.demo07;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 14:52
 */
public class EaterUpdate {
    public static void main(String[] args) {
        UpdatedChopstick c1 = new UpdatedChopstick("c1", new ReentrantLock());
        UpdatedChopstick c2 = new UpdatedChopstick("c2", new ReentrantLock());
        UpdatedChopstick c3 = new UpdatedChopstick("c3", new ReentrantLock());
        UpdatedChopstick c4 = new UpdatedChopstick("c4", new ReentrantLock());
        UpdatedChopstick c5 = new UpdatedChopstick("c5", new ReentrantLock());
        new UpdatedEater("亚里士多德", c1, c2).start();
        new UpdatedEater("苏格拉底", c2, c3).start();
        new UpdatedEater("尼采", c3, c4).start();
        new UpdatedEater("阿基米德", c4, c5).start();
        new UpdatedEater("柏拉图", c5, c1).start();
    }
}

@AllArgsConstructor
@Data
class UpdatedChopstick {
    private String name;
    private ReentrantLock lock;
}

@Slf4j
@AllArgsConstructor
class UpdatedEater extends Thread {
    private String name;
    private UpdatedChopstick left;
    private UpdatedChopstick right;
    
    @Override
    public void run() {
        while (true) {
            //log.info("{} <--- 尝试获取筷子 ---> {}..", this.name, this.left.getName());
            if (left.getLock().tryLock()) {
                //log.info("{} <--- 获取筷子 ---> {}..", this.name, this.left.getName());
                try {
                    if (right.getLock().tryLock()) {
                        //log.info("{} <--- 获取筷子 ---> {}..", this.name, this.right.getName());
                        try {
                            log.info("{} ---> 开始吃饭...", this.name);
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            right.getLock().unlock();
                        }
                    }
                } finally {
                    left.getLock().unlock();
                }
            }
        }
    }
}