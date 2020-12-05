package com.jielin.message.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SecuritySocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    protected boolean sameOriginDisabled() {
        return true;
    }

    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/app/**").permitAll();//permitAll();
    }
}
