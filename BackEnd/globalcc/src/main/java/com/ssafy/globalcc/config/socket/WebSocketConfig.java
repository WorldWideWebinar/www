package com.ssafy.globalcc.config.socket;

import com.ssafy.globalcc.domain.meetingCC.handler.MeetingCCHandler;
import com.ssafy.globalcc.domain.meetingSTT.Handler.MeetingSTTAudioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final MeetingCCHandler meetingCCHandler;
    private final MeetingSTTAudioHandler meetingSTTAudioHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(meetingSTTAudioHandler, "/api/meetingSTT/audio").setAllowedOriginPatterns("*");
        registry.addHandler(meetingCCHandler, "/meetingcc").setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024 * 1024);
        container.setMaxBinaryMessageBufferSize(1024 * 1024);
//        log.info("Websocket factory returned");
        return container;
    }


}