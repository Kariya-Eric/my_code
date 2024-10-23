package org.kariya.demo07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/22 16:36
 */

@Slf4j
public class AlternatePrint {
    
    public static void main(String[] args) {
        demo03();
    }
    
    private static Boolean print = false;
    
    private static int count = 0;
    
    public static void demo01() {
        new Thread(() -> {
            while (true) {
                synchronized (AlternatePrint.class) {
                    while (print) {
                        try {
                            AlternatePrint.class.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    print = true;
                    log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    AlternatePrint.class.notify();
                }
            }
        }, "t1").start();
        new Thread(() -> {
            while (true) {
                synchronized (AlternatePrint.class) {
                    while (!print) {
                        try {
                            AlternatePrint.class.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    print = false;
                    log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    AlternatePrint.class.notify();
                }
            }
        }, "t2").start();
    }
    
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition cond = lock.newCondition();
    
    public static void demo02() {
        new Thread(() -> {
            while (true) {
                lock.lock();
                while (count % 3 != 0) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                cond.signalAll();
            }
        }, "t1").start();
        new Thread(() -> {
            while (true) {
                lock.lock();
                while (count % 3 != 1) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                cond.signalAll();
            }
        }, "t2").start();
        new Thread(() -> {
            while (true) {
                lock.lock();
                while (count % 3 != 2) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                cond.signalAll();
            }
        }, "t3").start();
    }
    
    static Thread t1;
    static Thread t2;
    
    public static void demo03() {
        t1 = new Thread(() -> {
            while (true) {
                if (count % 2 != 0) {
                    LockSupport.park();
                }
                log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    LockSupport.unpark(t2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t2 = new Thread(() -> {
            while (true) {
                if (count % 2 != 1) {
                    LockSupport.park();
                }
                log.info("{} ---> {}", Thread.currentThread().getName(), count++);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    LockSupport.unpark(t1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2");
        t2.start();
        t1.start();
    }
}


