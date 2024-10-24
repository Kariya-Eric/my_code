package org.kariya.demo08;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/10/24 15:38
 */
public class MyBalkingTest {
    
    public static void main(String[] args) {
    
    }
    
    static volatile boolean initialized = false;
    
    //希望doInit方法仅被调用一次
    public static void demo01() {
        //需要使用synchronized保证原子性
        synchronized (MyBalkingTest.class) {
            if (initialized) {
                return;
            }
            doInit();
            initialized = true;
        }
    }
    
    private static void doInit() {
    }
}
