package org.kariya.demo01;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @Description JUC
 * @Author Kariya
 * @Date 2024/9/27 16:33
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class MyBenchmarkTest {
    static int[] ARRAY = new int[1000_000_00];
    
    static {
        Arrays.fill(ARRAY, 1);
    }
    
    @Benchmark
    public int c() throws ExecutionException, InterruptedException {
        int[] array = ARRAY;
        FutureTask<Integer> t1 = new FutureTask<>(() -> {
            int total = 0;
            for (int i = 0; i < array.length / 4; i++) {
                total += array[i];
            }
            return total;
        });
        FutureTask<Integer> t2 = new FutureTask<>(() -> {
            int total = 0;
            for (int i = array.length / 4; i < array.length / 2; i++) {
                total += array[i];
            }
            return total;
        });
        FutureTask<Integer> t3 = new FutureTask<>(() -> {
            int total = 0;
            for (int i = array.length / 2; i < array.length / 4 * 3; i++) {
                total += array[i];
            }
            return total;
        });
        FutureTask<Integer> t4 = new FutureTask<>(() -> {
            int total = 0;
            for (int i = array.length / 4 * 3; i < array.length; i++) {
                total += array[i];
            }
            return total;
        });
        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
        new Thread(t4).start();
        return t1.get() + t2.get() + t3.get() + t4.get();
    }
    
    @Benchmark
    public int d() throws ExecutionException, InterruptedException {
        int[] array = ARRAY;
        FutureTask<Integer> t1 = new FutureTask<>(() -> {
            int total = 0;
            for (int i = 0; i < array.length; i++) {
                total += array[i];
            }
            return total;
        });
        new Thread(t1).start();
        return t1.get();
    }
    
    public static void main(String[] args) throws RunnerException {
        Options build = new OptionsBuilder().include(MyBenchmarkTest.class.getSimpleName()).build();
        new Runner(build).run();
    }
}
