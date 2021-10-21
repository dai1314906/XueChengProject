package com.xuecheng.manage_course.dao.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientDemo01 {
    public static void main(String[] args) throws Exception {
        //建立socket
        DatagramSocket socket = new DatagramSocket();
        //建立包
        String msg = "你好，服务器";
        InetAddress localhost = InetAddress.getByName("localhost");
        int port = 9090;
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), 0, msg.getBytes().length, localhost, 9090);

        //发送
        socket.send(packet);
        //关闭
        socket.close();
    }
}
