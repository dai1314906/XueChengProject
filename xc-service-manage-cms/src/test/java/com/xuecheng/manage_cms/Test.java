package com.xuecheng.manage_cms;

public class Test {
    public static void main(String[] args) {
        int a = 0,b=1;
        a = (b++) + (++b) - (--b);
        System.out.println(a);
        System.out.println(b);
    }
}
