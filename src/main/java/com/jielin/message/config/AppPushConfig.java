package com.jielin.message.config;

import com.jielin.message.synpush.AppMsgPushHandler;
import com.jielin.message.synpush.UniPush.UniPushHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * app推送配置类
 *
 * @author yxl
 */
@Configuration
@Getter
@Setter
public class AppPushConfig {

    //极光推送配置类
    @Autowired
    private JgPushConfig jgPush;

    //个推推送配置类
    @Autowired
    private UniPushConfig uniPush;


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


    //当用户没有配置默认的app推送处理器时，使用个推sdk处理qpp推送,
    // 需要什么app处理handler,配置对应的javabean
    @Bean
    @ConditionalOnMissingBean(AppMsgPushHandler.class)
    public UniPushHandler uniPushHandler() {
        return new UniPushHandler();
    }
}
