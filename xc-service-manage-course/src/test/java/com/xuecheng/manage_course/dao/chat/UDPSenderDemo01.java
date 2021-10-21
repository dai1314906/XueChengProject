package com.xuecheng.manage_course.dao.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UDPSenderDemo01 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(8888);
        //准备数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            String data = reader.readLine();
            byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, new InetSocketAddress("localhost", 6666));
            socket.send(packet);
            if (data.equals("bye")){
                break;
            }
        }
        socket.close();

    }
}
