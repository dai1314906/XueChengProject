package com.dl.demo01;

public class TestThread4 implements Runnable {
    //票
    private int ticketName = 10;

    @Override
    public void run() {
        while (true){
            if (ticketName<=1){
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"-->拿到第"+ticketName-- +"--票");
        }
    }

    public static void main(String[] args) {
        TestThread4 ticket = new TestThread4();
        new Thread(ticket,"AAA").start();
        new Thread(ticket,"BBB").start();
        new Thread(ticket,"CCC").start();

    }
}
