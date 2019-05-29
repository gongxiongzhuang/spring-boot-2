package com.springboot.thread;

/**
 * @Description TODO
 * @Date 2019/5/8 17:10
 * @Created by gongxz
 */
public class Mytheard1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 30; i++) {
            System.out.println("thread#1===" + i);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}