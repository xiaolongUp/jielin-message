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

    //公众号接口accessToken
    private String accessToken;

    //获取公众号接口访问的access_token
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    //推送模版消息的接口
    public static final String PUSH_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    //订单创建成功的消息模版id
    public static final String CREATE_ORDER_SUCCESS = "KwPM9df4WyAJHwu4pp1B7_4s0ZmSb2DFqdXrqbRCUVA";

    //派单成功的消息模版id
    public static final String DISPATCH_ORDER_SUCCESS = "IuBdlu8fIvqE-JaEdvVN6GY35ljhrtahgbD_D8FLmPg";

    //出发的消息模版id
    public static final String DEPART_ORDER = "VRFqz1C06QwF_oLySSahYEamhnIy4-2Nb-yZYtFCJdI";

    //订单完成的消息模版id
    public static final String ORDER_FINISH = "Wlw3zIEBdcSFG1VgP24Z7KUJe6kJGkDbHxJ2qmGdnFk";

    //代客下单成功的消息模版id
    public static final String ASSIST_CREATE_ORDER_SUCCESS = "MCB3u1ZJzln6ogYsAqpjRMB4FAWwx12lz3CMlUkKAAg";

    //服务组出发的消息模版id
    public static final String GROUP_DEPART_ORDER = "";

    //派单成功，发送短信给用户的消息模版id
    public static final String DISPATCH_ORDER_SUCCESS_CUSTOMER = "t7bdqsJv9ymzWiUGNSuWCKxCqvfaScOphaZNB_ac3Tg";

    //周期订余量不足提醒的消息模版id
    public static final String REST_CYCLE_ORDER_REMIND = "";

    //切换服务者的消息模版id
    public static final String CHANGE_SERVICE = "YucqZrypDmp_Vz75PLu4u8lkGOYzLWm7DIWFPVuyHuA";

    //周期订单子订单取消通知客户的消息模版id
    public static final String CANCEL_CYCLE_SINGLE_ORDER = "";

    //取消订单通知客户的消息模版id
    public static final String CANCEL_ORDER = "eYcvOL83Nxfuzl5kgY_nONdQIy9eJ4MF8TjOgPzKLyc";

    //取消订单通知客服的消息模版id
    public static final String CANCEL_ORDER_NOTIFY_CUSTOM_SERVICE = "YucqZrypDmp_Vz75PLu4u8lkGOYzLWm7DIWFPVuyHuA";

    //取消订单通知悦姐的消息模版id
    public static final String CANCEL_ORDER_NOTIFY_SERVICE = "QkLOCasu-PtMhuvUVSvYX_AEfemLF5iv1_BrRRYwZHA";

}
