package com.wyh;

public class TicketSale {
    public static void main(String[] args) {
        Ticket12306 ticket12306 = new Ticket12306();

        //模拟多个服务消费者客户端
        Thread thread1 = new Thread(ticket12306, "携程");
        Thread thread2 = new Thread(ticket12306, "飞猪");
        thread1.start();
        thread2.start();
    }
}
