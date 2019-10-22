package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 极光推送配置文件
 *
 * @author yxl
 */
@Component
@Getter
@Setter
public class JgPushConfig {

    //appKey
    private String appKey;

    //密钥
    private String masterSecret;

}
