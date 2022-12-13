package com.wyh.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class CuratorTest {

    private CuratorFramework client;

    @Test
    @Before
    public void ConnectTest() {
        //1.
//        CuratorFramework client = CuratorFrameworkFactory.newClient(
//                "127.0.0.1:2181",
//                60 * 1000,
//                60 * 1000,
//                new ExponentialBackoffRetry(3000, 10)
//        );//连接字符串；会话超时时间（毫秒）；连接超时时间（毫秒）；重试策略

        //2.
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(60 * 1000)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .namespace("wyh")//名称空间 - 相当于简化默认为根目录 之后的添加节点都会在这个目录之下
                .build();

        client.start();
    }

    @Test
    public void AddTest() throws Exception {
        //1.默认永久不带数据
//        String path = client.create().forPath("/test1");//创建的节点没有数据，那么会将当前客户端的ip作为数据保存
//        System.out.println(path);

        //2.带数据
//        String path = client.create().forPath("/test2","Wangyihuan".getBytes());//创建的节点没有数据，那么会将当前客户端的ip作为数据保存
//        System.out.println(path);

        //3.临时节点
//        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/test2","Wangyihuan".getBytes());//创建的节点没有数据，那么会将当前客户端的ip作为数据保存
//        System.out.println(path);

        //4.多级节点创建
        String path = client.create().creatingParentsIfNeeded().forPath("/test3/p1","Wangyihuan".getBytes());//创建的节点没有数据，那么会将当前客户端的ip作为数据保存
        System.out.println(path);
    }

    @Test
    public void FindTest() throws Exception {
        //1.查询节点数据
//        System.out.println(new String(client.getData().forPath("/test1")));
        //2.查询子节点
//        System.out.println(client.getChildren().forPath("/"));
        //3.查询节点状态信息
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/test1");
        System.out.println(stat);
    }

    @Test
    public void ModifyTest() throws Exception {
        //1.
        client.setData().forPath("/test1","wyh".getBytes());
        //2.根据版本修改
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/test1");//version是查询出来的
        client.setData().withVersion(stat.getVersion()).forPath("/test1","wyh".getBytes());
    }

    @Test
    public void DeleteTest() throws Exception {
        //1.删除单个
//        client.delete().forPath("/test1");
        //2.删除带有子节点的节点
//        client.delete().deletingChildrenIfNeeded().forPath("/test2");
        //3.确保必须删除成功
//        client.delete().guaranteed().forPath("/test1");
        //4.回调
        client.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("删除后执行的操作...");
            }
        }).forPath("/test1");
    }

    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
