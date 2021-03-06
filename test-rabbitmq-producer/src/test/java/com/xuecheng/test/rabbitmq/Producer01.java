package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer01 {
    //队列
    private static final String QUEUE = "Hello World";

    public static void main(String[] args) {


        //通过连接工厂创建的连接与mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        //建立新连接

        Connection connection = null;
        Channel channel = null;
        try {
            //建立连接
            connection = connectionFactory.newConnection();

            //创建会话通道,生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            //声明队列，如果队列在mq中没有则创建
            //参数:String queue,boolean durable, boolean exclusive, boolean autoDelete,Map<String，Object> arguments
            /**
             * 参数明细
             * 1.queue 队列名称
             * 2.durable 是否持久化，如果持久化，mq重启队列还在
             * 3.exclusive 是否排他（独占连接），队列只允许在该连接中访问，如果connection连接关闭连接队列自动删除，如果将此参数设置为true可用于临时队列的创建
             * 4.autoDelete自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除)
             * 5.arguments 参数，可以设置一个队列的扩展参数，比如：设置存活时间
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //String exchange,String routingKey，BasicProperties props，byte[] body
            /**
             * 参数明细
             * 1.exchange:交换机,如果不指定将使用mq的默认交换机(设置为"")
             * 2.routingKey，路由key，交换机根据路由key来将消息转发到指定的队列，如果使用默认交换机，routingKey设置为队列的名称
             * 3.props 消息属性
             * 4.body 消息内容
             */
            String message = "hello dl";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to mq"+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
