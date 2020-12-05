package com.jielin.message.websocket.config;

import com.jielin.message.util.constant.WsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Map;

/**
 * 自定义HandshakeHandler{@link org.springframework.web.socket.server.support.DefaultHandshakeHandler}，浏览器通过stomp建立连接时，自定义用户信息的保存策略
 * 实现“生成自定义的{@link java.security.Principal}”
 */
@Component
@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * 建立连接时，塞入自定义的用户连接信息
     *
     * @param request    ServletServerHttpRequest
     * @param wsHandler  wsHandler
     * @param attributes attributes
     * @return 自定义用户信息 {@link CustomWebSocketPrincipal}
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("新的用户尝试连接了~~");
        if (!(request instanceof ServletServerHttpRequest)) {
            logger.error("登录失败，禁止连接WebSocket");
            return null;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String userName = servletRequest.getParameter(WsConstant.WEBSOCKET_USER);
        if (StringUtils.isBlank(userName)) {
            logger.error("登录失败，禁止连接WebSocket");
            return null;
        } else {
            logger.error(MessageFormat.format("连接成功，WebSocket连接开始创建Principal，用户：{0}", userName));
            return new CustomWebSocketPrincipal(userName);
        }
    }
}
