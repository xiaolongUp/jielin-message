package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * app推送配置类
 *
 * @author yxl
 */
@Configuration
@ConfigurationProperties(prefix = "app-push")
@Getter
@Setter
public class AppPushConfig {

    //极光推送配置类
    private JgPushConfig jgPush;

    //个推推送配置类
    private UniPushConfig uniPush;
}
