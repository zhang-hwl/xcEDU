package com.xuecheng.test.rabitmq;

import com.rabbitmq.client.*;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序，消费方
 */
public class Consumer01 {

    //声明队列，与生产方一致
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws IOException, TimeoutException {

        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置mq服务器ip地址
        connectionFactory.setHost("127.0.0.1");
        //输入端口
        connectionFactory.setPort(5672);
        //登录用户名,密码
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机,一个mq的服务可以设置多个虚拟机,每一个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        //建立连接
        Connection connection =connectionFactory.newConnection();
        //创建会话通道
        Channel channel = connection.createChannel();

        //监听队列
        //声明队列,如果队列在mq当中没有则要创建
        /**
         * queue：队列名称
         * durable:是否持久化，如果持久化，mq重启后队列还在
         * exclusive 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列自动删除，如果将此参数设置true可用临时队列的创建
         * autoDelete 自动删除，队列不在使用时是否自动删除此队列，如果将此参数和exclusive设置为true就可以实现临时队列（队列不用了就自动删除）
         * arguments参数，可以设置一些扩展参数
         */
        channel.queueDeclare(QUEUE,true,false,false,null);

        //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

            /**
             * 当接收到消息后，此方法将被调用
             * @param consumerTag 消费者标签，用来标识消费者，可以在监听队列的时候设置channel.basicConsume
             * @param envelope 信封，可以拿到交换机
             * @param properties 属性
             * @param body 消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //拿到交换机
                String exchange = envelope.getExchange();
                //消息id，mq在channel中用来标识消息的id，可以用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body,"utf-8");
                System.out.println("receive message:" +message);
            }
        };

        //监听队列
        //String queue, boolean autoAck, Consumer callback
        /**
         * queue队列名称
         * autoAck自动回复，当消费者接收到消息后，要告诉mq消息已经接收，如果将此参数设置为true，表示自动回复mq，否则要通过编程实现回复
         * callback消费方法，当消费者接受到消息要执行的方法
         */
        channel.basicConsume(QUEUE,true,defaultConsumer);







    }





























}
