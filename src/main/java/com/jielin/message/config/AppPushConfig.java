package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

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
}
