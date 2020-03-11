package com.jielin.message.config;

import com.jielin.message.synpush.app.AppMsgPushHandler;
import com.jielin.message.synpush.app.UniPush.UniPushHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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
    public RestTemplate restTemplate(ResponseErrorHandler responseErrorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }


    //当用户没有配置默认的app推送处理器时，使用个推sdk处理qpp推送,
    // 需要什么app处理handler,配置对应的javabean
    @Bean
    @ConditionalOnMissingBean(AppMsgPushHandler.class)
    public UniPushHandler uniPushHandler() {
        return new UniPushHandler();
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler() {
        return new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                boolean hasError = false;
                if (response.getStatusCode() != HttpStatus.OK) {
                    hasError = true;
                }
                return hasError;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("restTemplate调用接口异常：" + response.getStatusCode().toString());
                }
            }
        };
    }
}
