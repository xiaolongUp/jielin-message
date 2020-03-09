package com.jielin.message.util.constant;

/**
 * 通用的常量类
 *
 * @author yxl
 */
public class MsgConstant {

    //微信的access_token的key名称
    public static final String WX_TOKEN_KEY = "access_token";

    //微信的access_token的key名称
    public static final String WX_MP_TOKEN_KEY = "mp_access_token";

    //token过期时间
    public static final String WX_EXPIRES_IN = "expires_in";

    //微信公众号的access_token
    public static final String WX_ACCESS_TOKEN = "wx_access_token";

    //微信小程序的access_token
    public static final String WX_MP_ACCESS_TOKEN = "wx_mp_access_token";

    //rabbitMq的消息推送队列名称（此处的消息队列是下单流程的消息推送）
    public static final String PUSH_MSG = "push_msg";

    //钉钉的消息推送队列
    public static final String DING_PUSH_MSG = "ding_push_msg";

    //用户小程序平台类型（小程序）
    public static final String PLATFORM_WECHAT_MP = "wechat_mp";

    //平台类型（公众号）
    public static final String PLATFORM_WECHAT_OA = "wechat_oa";

    //悦姐小程序
    public static final String YUEJIE_WECHAT_MP = "yj_wechat_mp";

    //测试环境
    public static final String TEST_PROFILE = "test";

    //开发环境
    public static final String DEV_PROFILE = "dev";

    //生产环境
    public static final String PROD_PROFILE = "prod";

}
