package com.jielin.message.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static com.jielin.message.util.constant.WsConstant.*;

//WebSocket的配置类
@Configuration
//开启对WebSocket的支持
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private CustomHandshakeHandler handshakeHandler;

    //注册一个STOMP协议的节点，并映射到指定的URL
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册一个STOMP的endpoint,并指定使用SockJS协议,创建连接的断点
        registry.addEndpoint(LOGIN_PATH)
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketPushInterceptor())
                .setHandshakeHandler(handshakeHandler)
                .withSockJS();
    }


    //配置消息代理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //服务端广播消息的路径前缀，客户端需要相应订阅/topic/yyy这个地址的消息
        registry.enableSimpleBroker(BROADCAST_PREFIX, P2P_PUSH_PREFIX);
        //客户端需要把消息发送到/app/xxx地址
        registry.setApplicationDestinationPrefixes(APP_REQUEST_PREFIX);
        //定义一对一推送的时候前缀
        registry.setUserDestinationPrefix(P2P_PUSH_PREFIX);
    }
}
