package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 极光推送配置文件
 *
 * @author yxl
 */
@Getter
@Setter
public class JgPushConfig {

    //appKey
    private String appKey;

    //密钥
    private String masterSecret;

}
