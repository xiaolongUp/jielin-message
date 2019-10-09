package com.jielin.message;

import com.jielin.message.synpush.AppMsgPushHandler;
import com.jielin.message.synpush.UniPush.UniPushHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@MapperScan("com.jielin.message.dao.mysql")
@EnableRetry
public class JielinMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(JielinMessageApplication.class, args);
    }

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
