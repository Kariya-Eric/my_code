package org.kariya.demo06;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/21 13:47
 */
public class Mails {
    public static void main(String[] args) throws InterruptedException {
        new User<String>().start();
        new User<Integer>().start();
        new User<String>().start();
        TimeUnit.SECONDS.sleep(1);
        new Poster<Integer>(2, 10).start();
        TimeUnit.SECONDS.sleep(3);
        new Poster<Integer>(1, 15).start();
        TimeUnit.SECONDS.sleep(2);
        new Poster<String>(3, "World").start();
    }
}

@Slf4j
@AllArgsConstructor
class Poster<T> extends Thread {
    private int id; //邮箱id
    private T t; //信件内容
    
    @Override
    public void run() {
        Response<T> boxes = MailBoxes.getBoxes(id);
        log.info("邮递员 {} 准备送信...", boxes.getId());
        boxes.setResp(t);
        log.info("邮递员 {} ---> 送信 {}...", boxes.getId(), t);
    }
}

@Slf4j
class User<T> extends Thread {
    @Override
    public void run() {
        Response<T> resp = MailBoxes.create();
        log.info("用户 {} 准备收信...", resp.getId());
        T t = resp.getResp();
        log.info("用户 {} ---> 收信 {}", resp.getId(), t);
    }
}

@Data
class MailBoxes<T> {
    private static Map<Integer, Response> boxes = new Hashtable<>();
    private static int id = 1;
    
    private static synchronized int generateId() {
        return id++;
    }
    
    public static <T> Response<T> getBoxes(Integer id) {
        return boxes.remove(id);
    }
    
    public static <T> Response<T> create() {
        Response<T> resp = new Response<>(generateId());
        boxes.put(resp.getId(), resp);
        return resp;
    }
    
    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

@Data
class Response<T> {
    private int id;
    private T t;
    
    public Response(Integer id) {
        this.id = id;
    }
    
    public void setResp(T t) {
        synchronized (this) {
            this.t = t;
            this.notifyAll();
        }
    }
    
    public T getResp() {
        synchronized (this) {
            while (this.t == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.t;
        }
    }
}
