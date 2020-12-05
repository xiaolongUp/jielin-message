package com.jielin.message.util.constant;

import com.jielin.message.websocket.pojo.AdminAlertContent;
import com.jielin.message.websocket.pojo.AdminHeaderContent;

/**
 * websocket连接使用常量
 */
public class WsConstant {

    /**
     * websocket建立连接时的用户信息标实
     */
    public static final String WEBSOCKET_USER = "userName";


    /**
     * websocket登录路径
     */
    public static String LOGIN_PATH = "/endpointSocket";

    /**
     * 订阅消息广播前缀
     */
    public static String BROADCAST_PREFIX = "/topic";

    /**
     * 订阅消息广播订阅地址
     */
    public static String BROADCAST_PATH = "/topic";

    /**
     * 点对点消息推送地址前缀
     */
    public static final String P2P_PUSH_PREFIX = "/user";


    /**
     * 点对点消息推送地址后缀,最后的地址为/user/用户识别码/msg
     */
    public static final String P2P_PUSH_SUFFIX = "/msg";

    /**
     * 浏览器发送消息前缀
     */
    public static final String APP_REQUEST_PREFIX = "/app";

    /**
     * 区分管理端的websocket推送哪种消息
     */
    public static final String TYPE = "type";

    /**
     * redis缓存key前缀
     */
    public static final String ALERT_PREFIX = AdminAlertContent.class.getSimpleName();

    /**
     * redis缓存key前缀
     */
    public static final String HEADER_PREFIX = AdminHeaderContent.class.getSimpleName();


}
