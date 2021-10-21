package com.xuecheng.manage_course.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddress {
    public static void main(String[] args) {
        try {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            System.out.println(inetAddress);
            InetAddress inetAddress1 = InetAddress.getByName("www.baidu.com");
            System.out.println(inetAddress1);

            System.out.println(inetAddress.getHostAddress());
            System.out.println(inetAddress.getCanonicalHostName());
            System.out.println(inetAddress.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
