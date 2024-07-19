package com.ssafy.globalcc.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //WebSocket 서버를 활성화
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("http://localhost:5000") // "*" 오류 발생
                .withSockJS();
        registry.addEndpoint("/stomp/stt")
                .setAllowedOrigins("http://localhost:5000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지 브로커가 해당 api를 구독하고 있는 클라이언트에게 메시지를 전달
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub"); //메시지를 발행하기 위한 prefix
    }
}