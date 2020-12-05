package com.jielin.message.websocket.config;

import com.jielin.message.util.RedisUtil;
import com.jielin.message.util.constant.WsConstant;
import com.jielin.message.websocket.WebSocketService;
import com.jielin.message.websocket.pojo.AdminAlertContent;
import com.jielin.message.websocket.pojo.SocketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 监听浏览器的连接和断开
 */
@Component
public class WebSocketSessionListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionListener.class.getName());

    private Set<String> onLineUsers = new CopyOnWriteArraySet<>();

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 监听连接事件，当有新的连接进来时，推送历史保存redis的消息给对应的客户端
     *
     * @param sce session连接事件
     */
    @EventListener
    public void connectionEstablished(SessionConnectedEvent sce) {
        MessageHeaders msgHeaders = sce.getMessage().getHeaders();
        CustomWebSocketPrincipal principal = (CustomWebSocketPrincipal) msgHeaders.get("simpUser");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(sce.getMessage());
        List<String> nativeHeaders = sha.getNativeHeader(WsConstant.WEBSOCKET_USER);
        if (principal != null) {
            String userId = principal.getName();
            onLineUsers.add(userId);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("用户：%s，已建立连接！ ", userId));
            }
        } else if (nativeHeaders != null) {
            String userId = nativeHeaders.get(0);
            onLineUsers.add(userId);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("用户：%s，已建立连接！ ", userId));
            }
        }
        logger.error("当前已建立websocket连接数: " + onLineUsers.size());
    }

    /**
     * 断开连接事件，断开连接时需要将数据保存在redis
     *
     * @param sde 断开事件
     */
    @EventListener
    public void webSocketDisconnect(SessionDisconnectEvent sde) {
        MessageHeaders msgHeaders = sde.getMessage().getHeaders();
        CustomWebSocketPrincipal principal = (CustomWebSocketPrincipal) msgHeaders.get("simpUser");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(sde.getMessage());
        List<String> nativeHeaders = sha.getNativeHeader("userId");
        if (principal != null) {
            String userId = principal.getName();
            onLineUsers.remove(userId);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("用户：%s，断开连接！ ", userId));
            }
        } else if (nativeHeaders != null) {
            String userId = nativeHeaders.get(0);
            onLineUsers.remove(userId);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("用户：%s，断开连接！ ", userId));
            }
        }
        logger.error("当前已建立websocket连接数: " + onLineUsers.size());
    }

    /**
     * 监听订阅事件，订阅后推送消息
     *
     * @param sde SessionSubscribeEvent
     */
    @EventListener
    public void webSocketSubscribe(SessionSubscribeEvent sde) {
        MessageHeaders msgHeaders = sde.getMessage().getHeaders();
        CustomWebSocketPrincipal principal = (CustomWebSocketPrincipal) msgHeaders.get("simpUser");
        if (principal != null) {
            String userId = principal.getName();
            Object content = redisUtil.get(WsConstant.HEADER_PREFIX + userId);
            if (content != null) {
                webSocketService.send2User(userId, new SocketResponse<>(content));
            }
            List<Object> list = redisUtil.listGet(WsConstant.ALERT_PREFIX + userId);
            if (list != null && list.size() > 0) {
                for (Object o : list) {
                    AdminAlertContent alertContent = (AdminAlertContent) o;
                    webSocketService.send2User(userId, new SocketResponse<>(alertContent));
                }
                redisUtil.del(WsConstant.ALERT_PREFIX + userId);
            }
        }
    }

    /**
     * 获取在线的用户列表集合，给浏览器推送消息时，查看用户是否在线
     *
     * @return 在线用户列表集合
     */
    public Set<String> getOnLineUsers() {
        return onLineUsers;
    }
}
