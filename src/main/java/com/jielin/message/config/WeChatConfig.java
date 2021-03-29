package com.jielin.message.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    /**
     * 所有的公众号
     */
    List<WeChatGzh> weChatGzhs;


    /**
     * 所有的小程序
     */
    List<WeChatMp> weChatMps;

    //获取access_token
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    //公众号推送模版消息的接口
    public static final String PUSH_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    //小程序推送模版消息的接口
    public static final String MP_PUSH_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";

    /**
     * 微信公众号
     * systemApply的类型与数据库配置的push表当中的类型一致
     *
     * @see com.jielin.message.util.enums.UserTypeEnum
     */
    @Data
    public static class WeChatGzh {

        /**
         * 适用系统
         */
        private String systemApply;

        private String appid;

        private String appsecret;

        private String accessToken;

        /**
         * 判断当前的用户类型是否适用该公众号
         *
         * @param type 用户类型
         * @return 该用户类型是否适用该公众号
         */
        public Boolean support(String type) {
            return this.systemApply.equalsIgnoreCase(type);
        }

    }

    /**
     * 微信小程序
     * systemApply的类型与数据库配置的push表当中的类型一致
     *
     * @see com.jielin.message.util.enums.UserTypeEnum
     */
    @Data
    public static class WeChatMp {

        /**
         * 适用系统
         */
        private String systemApply;

        private String appid;

        private String appsecret;

        private String accessToken;

        /**
         * 公众号底部跳转小程序链接
         */
        private String pagepath;

        /**
         * 判断当前的用户类型是否适用该公众号
         *
         * @param type 用户类型
         * @return 该用户类型是否适用该公众号
         */
        public Boolean support(String type) {
            return this.systemApply.equalsIgnoreCase(type);
        }

    }
}
