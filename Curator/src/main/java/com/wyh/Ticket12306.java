package com.wyh;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class Ticket12306 implements Runnable{
    private int tickets = 10;//模拟数据库里的票数

    private final InterProcessMutex lock;//锁

    public Ticket12306() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(60 * 1000)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .build();
        client.start();
        lock = new InterProcessMutex(client,"/lock");
    }

    @Override
    public void run() {
        while (true) {
            //首先获取锁
            try {
                lock.acquire(3, TimeUnit.SECONDS);//三秒获取一次
                if (tickets > 0) {
                    System.out.println(Thread.currentThread()+": 当前还剩票数" + tickets);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    lock.release();//释放锁
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
