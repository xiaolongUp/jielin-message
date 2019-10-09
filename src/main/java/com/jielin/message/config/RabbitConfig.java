package com.jielin.message.config;

import com.jielin.message.util.MsgConstant;
import org.springframework.amqp.core.Queue;
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
    public Queue Queue() {
        return new Queue(MsgConstant.PUSH_MSG);
    }

    /**
     * 定义消息转换实例
     */
    @Bean
    MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
