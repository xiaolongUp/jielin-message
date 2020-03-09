package com.jielin.message.config;

import com.jielin.message.util.constant.MsgConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq 配置类
 *
 * @author yxl
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue queue() {
        return new Queue(MsgConstant.PUSH_MSG);
    }

    @Bean
    public Queue retryQueue() {
        return new Queue(MsgConstant.RETRY_PUSH_MSG);
    }

    @Bean
    public Queue failQueue() {
        return new Queue(MsgConstant.FAIL_PUSH_MSG);
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
