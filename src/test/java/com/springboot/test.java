package com.springboot;

import com.springboot.comm.cache.CacheHashCode;
import com.springboot.thread.MyPool;
import com.springboot.thread.Mytheard1;
import com.springboot.thread.Mytheard2;
import com.springboot.thread.ThreadPoolManager;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Date 2019/4/25 14:40
 * @Created by gongxz
 */
public class test {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        // 通过主线程启动自己的线程

        // 通过实现runnable接口
        Thread thread1 = new Thread(new Mytheard1());
        //thread1.setDaemon(true);//守护线程
        thread1.start();

        // 通过继承thread类
        Thread thread2 = new Thread(new Mytheard2());
        //thread2.setDaemon(true);
        thread2.start();

        // 注意这里不是调运run()方法，而是调运线程类Thread的start方法，在Thread方法内部，会调运本地系统方法，最终会自动调运自己线程类的run方法

        System.out.println("mainThread isDaemon:"
                + Thread.currentThread().isDaemon());
        System.out.println("thread1 isDaemon:" + thread1.isDaemon());
        System.out.println("thread2 isDaemon:" + thread2.isDaemon());

        // 让主线程睡眠
        Thread.sleep(1000L);
        System.out.println("主线程结束！用时："
                + (System.currentTimeMillis() - startTime));
        //System.exit(0);
    }

    @Test
    public void testThreadPool() throws InterruptedException {
        ThreadPoolExecutor pool1 = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        pool1.execute(() -> System.out.println("快捷线程池中的线程！"));

        ThreadPoolExecutor pool2 = MyPool.getInstance().getThreadPool();
        pool2.execute(() -> {
            System.out.println("pool2 普通线程池中的线程！");
            try {
                Thread.sleep(30*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("pool2 poolSize:"+pool2.getPoolSize());
        System.out.println("pool2 corePoolSize:"+pool2.getCorePoolSize());
        System.out.println("pool2 largestPoolSize:"+pool2.getLargestPoolSize());
        System.out.println("pool2 maximumPoolSize:"+pool2.getMaximumPoolSize());

        /*ThreadPoolExecutor pool3 = MyPool.getInstance().getThreadPool();
        pool3.execute(() -> System.out.println("pool3 普通线程池中的线程！"));
        System.out.println("pool3 poolSize:"+pool3.getPoolSize());
        System.out.println("pool3 corePoolSize:"+pool3.getCorePoolSize());
        System.out.println("pool3 largestPoolSize:"+pool3.getLargestPoolSize());
        System.out.println("pool3 maximumPoolSize:"+pool3.getMaximumPoolSize());*/

        ScheduledThreadPoolExecutor pool3 = MyPool.getInstance().getScheduledPool();
        pool3.scheduleAtFixedRate(() -> System.out.println("计划任务线程池中的线程！"), 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testThreadPoolManager() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int corePoolSize = threadPoolTaskExecutor.getCorePoolSize();
        int maxPoolSize = threadPoolTaskExecutor.getMaxPoolSize();
        System.out.println("corePoolSize:"+corePoolSize);
        System.out.println("maxPoolSize:"+maxPoolSize);
        /*ThreadPoolManager threadPoolManager = ThreadPoolManager.newInstance();
        threadPoolManager.perpare();
        for (int i = 0; i < 3000; i++) {
            threadPoolManager.addExecuteTask(()->{
                System.out.println("正在执行task " + Thread.currentThread().getName());
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("task " + Thread.currentThread().getName() + "执行完毕");
            });
            System.out.println("线程池中线程数目：" + threadPoolManager.getPoolSize() + "，队列中等待执行的任务数目："
                    + threadPoolManager.getQueue() + "，已执行玩别的任务数目：" + threadPoolManager.getCompletedTaskCount());
        }*/
        //threadPoolManager.shutdown();
    }

    @Test
    public void testKey() {
        //System.out.println(CacheHashCode.of().toString());
    }
}