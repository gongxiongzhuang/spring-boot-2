package com.springboot.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description 线程池
 * @Date 2019/5/9 14:15
 * @Created by gongxz
 */
public class MyPool {
    private static MyPool myPool = null;
    //单例线程池中有两种具体的线程池
    private ThreadPoolExecutor threadPool = null;
    private ScheduledThreadPoolExecutor scheduledPool = null;

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public ScheduledThreadPoolExecutor getScheduledPool() {
        return scheduledPool;
    }

    //设置线程池的各个参数的大小
    private int corePoolSize = 10;// 池中所保存的线程数，包括空闲线程。
    private int maximumPoolSize = 20;// 池中允许的最大线程数。
    private long keepAliveTime = 3;// 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
    private int scheduledPoolSize = 10;

    private static synchronized void create() {
        if (myPool == null)
            myPool = new MyPool();
    }

    public static MyPool getInstance() {
        if (myPool == null)
            create();
        return myPool;
    }

    private MyPool() {
        //实例化线程池，这里使用的 LinkedBlockingQueue 作为 workQueue ，使用 DiscardOldestPolicy 作为 handler
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());//不在新线程中执行任务，而是由调用者所在的线程来执行
        //实例化计划任务线程池
        this.scheduledPool = new ScheduledThreadPoolExecutor(scheduledPoolSize);
    }
}
