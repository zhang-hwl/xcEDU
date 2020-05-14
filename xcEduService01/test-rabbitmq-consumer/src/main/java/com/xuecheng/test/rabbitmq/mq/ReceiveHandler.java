package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author zs
 * @version 1.0
 * @create 2020/5/12 20:43
 */
@Component//交给spring容器管理
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.ROUTINGKEY_EMAIL})
    public void send_email(String msg, Message message, Channel channel){

        System.out.println("receive message:"+msg);
    }
















}
