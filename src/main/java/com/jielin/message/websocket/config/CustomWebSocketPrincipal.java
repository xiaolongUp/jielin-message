package com.jielin.message.websocket.config;

import java.security.Principal;

/**
 * 自定义{@link java.security.Principal}
 * 当选择对单一用户进行浏览器消息推送时需要使用该用户信息
 */
public class CustomWebSocketPrincipal implements Principal {

    /**
     * 系统名称+用户id，例如：web-admin240（前期只使用了用户id）
     */
    private String userName;

    public CustomWebSocketPrincipal(String userName) {
        this.userName = userName;
    }

    @Override
    public String getName() {
        return userName;
    }
}
