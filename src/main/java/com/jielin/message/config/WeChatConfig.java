package com.jielin.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众和小程序配置文件
 *
 * @author yxl
 */
@Configuration
@ConfigurationProperties(prefix = "wechat")
@Getter
@Setter
public class WeChatConfig {

    //公众号的appid和secret
    private String gzhAppid;

    private String gzhAppsecret;

    //悦姐小程序的appid和secret
    private String yjMpAppid;

    private String yjMpAppsecret;

    //公众号接口accessToken
    private String gzhAccessToken;

    //小程序接口accessToken
    private String mpAccessToken;

    //获取access_token
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    //公众号推送模版消息的接口
    public static final String PUSH_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    //小程序推送模版消息的接口
    public static final String MP_PUSH_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
}
