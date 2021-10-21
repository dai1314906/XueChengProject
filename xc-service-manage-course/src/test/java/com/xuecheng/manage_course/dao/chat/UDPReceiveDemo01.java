package com.xuecheng.manage_course.dao.chat;


import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiveDemo01 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(6666);
        while (true){
            //准备接收
            byte[] container = new byte[1024];
            DatagramPacket packet = new DatagramPacket(container, 0, container.length);
            socket.receive(packet);

            //断开
            byte[] data = packet.getData();
            String receiveData = new String(data,0, data.length);
            System.out.println(receiveData);
            if (receiveData.equals("bye")){
                break;
            }

        }
        socket.close();
    }
}
