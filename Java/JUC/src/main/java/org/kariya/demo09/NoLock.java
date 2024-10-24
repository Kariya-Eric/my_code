package org.kariya.demo09;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/24 16:41
 */

@Slf4j
public class NoLock {
    
    
    public static void main(String[] args) {
        demo02();
    }
    
    public static void demo01() {
        Account account = new AccountUnsafe(10000);
        Account.demo(account);
    }
    
    public static void demo02() {
        Account account = new AccountSafe(new AtomicInteger(10000));
        Account.demo(account);
    }
}

@Slf4j
@Data
@AllArgsConstructor
class AccountSafe implements Account {
    
    private AtomicInteger balance;
    
    @Override
    public Integer getBalance() {
        return this.balance.get();
    }
    
    @Override
    public void withdraw(Integer amount) {
        while (true) {
            int prev = balance.get(); //获取余额最新值
            int next = prev - amount; //修改后的余额
            //修改后的余额同步
            //比较并设置值 compareAndSwap CAS CPU指令级别的，可以保证为原子性
            if (balance.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}


@Slf4j
@Data
@AllArgsConstructor
class AccountUnsafe implements Account {
    
    private Integer balance;
    
    @Override
    public Integer getBalance() {
        return this.balance;
    }
    
    @Override
    public void withdraw(Integer amount) {
        //临界区代码，不加锁会出现线程安全问题
        this.balance -= amount;
    }
}

interface Account {
    //获取余额
    Integer getBalance();
    
    //取款
    void withdraw(Integer amount);
    
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        long start = System.currentTimeMillis();
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        long end = System.currentTimeMillis();
        System.out.println(account.getBalance() + " ----> " + (end - start) + "ms");
    }
}