package org.kariya.demo04;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/17 13:42
 */
@Slf4j
@AllArgsConstructor
@Data
public class Account {
    private int money;
    
    public void transfer(Account target, int amount) {
        //target和this都是共享变量，需要找出相同的共享变量
        synchronized (Account.class) {
            if (this.money >= amount) {
                this.setMoney(this.getMoney() - amount);
                target.setMoney(target.getMoney() + amount);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, 500);
            }
        }, "a");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, 500);
            }
        }, "b");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("转账后总金额 {}", a.getMoney() + b.getMoney());
    }
}
