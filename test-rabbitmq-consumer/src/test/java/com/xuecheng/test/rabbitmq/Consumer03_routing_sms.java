package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer03_routing_sms {

//    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";

    private static final String ROUTINGKEY_SMS = "inform_sms";

    public static void main(String[] args) throws IOException, TimeoutException {
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
        channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
//            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

        //声明一个交换机
        //参数： string exchange,string type
        /**
         * 参数明细
         * 1.交换机的名称
         * 2.交换机的类型
         * fanout: 对应的工作模式为publish/subscribe
         * direct: 对应的是Routing工作模式
         * topic:对应的是topic工作模式
         * headers:对应的是headers工作模式
         */
        channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
        //进行交换机和队列绑定
        //参数: String queue,String exchange,String routingKey
        /**
         * 参数明细
         * 1.queue 队列名称
         * 2.exchange 交换机名称
         * 3.routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中,在发布订阅中协调为空字符串
         */
        channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
//            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");


        //监听队列，如果队列在mq中没有则创建
        //参数:String queue,boolean durable, boolean exclusive, boolean autoDelete,Map<String，Object> arguments
        /**
         * 参数明细
         * 1.queue 队列名称
         * 2.durable 是否持久化，如果持久化，mq重启队列还在
         * 3.exclusive 是否排他（独占连接），队列只允许在该连接中访问，如果connection连接关闭连接队列自动删除，如果将此参数设置为true可用于临时队列的创建
         * 4.autoDelete自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除)
         * 5.arguments 参数，可以设置一个队列的扩展参数，比如：设置存活时间
         */
//        channel.queueDeclare(QUEUE,true,false,false,null);
        //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {


            /**
             * 当接受到消息后此方法被调用
             * @param consumerTag 消费者标签，用来标识消费者，在监听队列时设置channel,basicConsume
             * @param envelope  信封
             * @param properties 消息属性
             * @param body 消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //super.handleDelivery(consumerTag, envelope, properties, body);

                //获取交换机
                String exchange = envelope.getExchange();
                //消息id,mq在channel中用来标识消息id，可用于确认消息已接受
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body, "utf-8");
                System.out.println("receive message:" + message);
            }
        };
        //监听队列
        /**
         * String queue,boolean autoAck,Consumer callback
         * 参数明细
         * 1.queue 队列名称
         * 2.atuoAck autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果将此参数设置为tru表示会自动回复mq，如果设置为false要通过编程实现回复
         * 3.callback 消费方法，当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE_INFORM_SMS, true, defaultConsumer);
    }
}
