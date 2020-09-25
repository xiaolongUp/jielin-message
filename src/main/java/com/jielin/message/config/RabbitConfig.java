package com.jielin.message.config;

import com.jielin.message.util.constant.MsgConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.jielin.message.util.constant.MsgConstant.RETRY_PUSH_MSG;

/**
 * rabbitmq 配置类
 *
 * @author yxl
 */
@Configuration
public class RabbitConfig {

    /**
     * 死信队列的交换机
     */
    private final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";

    /**
     * 死信队列的路由key
     */
    private static final String DEAD_LETTER_QUEUE_ROUTING_KEY = "dead.letter.routingkey";

    //消费队列
    @Bean("push_msg")
    public Queue queue() {
        return new Queue(MsgConstant.PUSH_MSG);
    }

    //重试队列 basic.reject or basic.nack 时直接投递消息到死信队列
    @Bean("retry_push_msg")
    public Queue retryQueue() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange 这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        // x-dead-letter-routing-key 这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_ROUTING_KEY);
        return QueueBuilder.durable(RETRY_PUSH_MSG).withArguments(args).build();
    }

    // 声明死信Exchange
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    //死信队列
    @Bean("fail_push_msg")
    public Queue failQueue() {
        return new Queue(MsgConstant.FAIL_PUSH_MSG);
    }

    // 声明死信队列失败队列绑定关系
    @Bean
    public Binding deadLetterBinding(@Qualifier("retry_push_msg") Queue queue, @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_ROUTING_KEY);
    }

    /**
     * 定义消息转换实例
     */
    @Bean
    MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
