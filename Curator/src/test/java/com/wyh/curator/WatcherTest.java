package com.wyh.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.tree.Tree;

public class WatcherTest {
    private CuratorFramework client;
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
    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void NodeCacheTest() throws Exception {
        //1.创建NodeCache对象
        NodeCache nodeCache = new NodeCache(client, "/test1");
        //2.注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点changed");
                //节点改变之后的操作...
            }
        });
        //开启监听
        nodeCache.start(true);//true为开启监听时加载缓冲数据

        while (true) {}
    }

    @Test
    public void PathChildrenCacheTest() throws Exception {
        //1.创建NodeCache对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/test1",true);//true为开启监听时加载缓冲数据
        //2.注册监听
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("子节点changed");
                //节点改变之后的操作...
                //获取类型
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                //为update时的操作
                if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
                }
            }
        });
        //开启监听
        pathChildrenCache.start();

        while (true) {}
    }

    @Test
    public void TreeCacheTest() throws Exception {
        //1.创建NodeCache对象
        TreeCache treeCache = new TreeCache(client, "/test1");
        //2.注册监听
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("节点changed");
                //节点改变之后的操作...
            }
        });
        //开启监听
        treeCache.start();

        while (true) {}
    }
}
