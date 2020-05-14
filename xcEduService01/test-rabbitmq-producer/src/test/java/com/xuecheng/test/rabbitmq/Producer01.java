package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序，生产方
 */
public class Producer01 {

    //队列
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) {
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

        Connection connection =null;
        Channel channel = null;
        try {
            //建立连接
            connection = connectionFactory.newConnection();
            //创建会话通道,生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            //声明队列,如果队列在mq当中没有则要创建
            /**
             * queue：队列名称
             * durable:是否持久化，如果持久化，mq重启后队列还在
             * exclusive 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列自动删除，如果将此参数设置true可用临时队列的创建
             * autoDelete 自动删除，队列不在使用时是否自动删除此队列，如果将此参数和exclusive设置为true就可以实现临时队列（队列不用了就自动删除）
             * arguments参数，可以设置一些扩展参数
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息指定队列和交换机
            //String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * exchange:交换机，如果不指定则使用默认的交换机（设置为""）
             * routingKey：路由key，交换机根据路由key，将消息转发到指定的队列。如果使用默认的交换机，routingKey设置为队列的名称
             * props；设置消息的属性
             * body：消息的内容
             */
            String message = "hello rabbitmq 这是一条消息";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to mq"+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接，先关通道，再关连接
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

        //和mq建立连接
    }


















}
