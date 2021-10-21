package com.xuecheng.manage_course.dao;

public class Test {
    public static void main(String[] args) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();

        System.out.println("max="+maxMemory+"字节\t"+(maxMemory/(double)1024/1024)+"MB");
        System.out.println("max="+totalMemory+"字节\t"+(totalMemory/(double)1024/1024)+"MB");
    }

}
