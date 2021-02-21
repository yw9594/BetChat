package com.study.loge.betchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Web Socket handshake를 위한 주소입니다.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // chatting을 @MessageMapping Controller로 전달하기 위한 prefix를 설정합니다.
        registry.setApplicationDestinationPrefixes("/pub/chat");

        // chatting room을 subscribe하기 위한 prefix를 설정합니다.
        registry.enableSimpleBroker("/sub/chat");
    }
}
