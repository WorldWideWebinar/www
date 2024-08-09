package com.ssafy.globalcc.domain.meetingSTT.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

@Component
public class MeetingSTTAudioHandler extends AbstractWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(MeetingSTTAudioHandler.class);
    @Value("${gpu.server.url}")
    private String gpuServerUrl;


    private WebSocketSession gpuSocketSession;
    private WebSocketSession clientSocketSession;
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        try {
           WebSocketClient webSocketClient = new StandardWebSocketClient();
           clientSocketSession = session;
           gpuSocketSession = webSocketClient.execute(new WebSocketHandler() {
               @Override
               public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("Connected to gpu server");
               }

               @Override
               public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                   log.info("Message received from gpu: " + message.getPayload());
                   clientSocketSession.sendMessage(message);
               }

               @Override
               public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    log.error("Transport error", exception);
               }

               @Override
               public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                   log.info("GPU Connection closed : {}", closeStatus);
                    if(clientSocketSession.isOpen()){
                        clientSocketSession.close(closeStatus);
                    }
               }

               @Override
               public boolean supportsPartialMessages() {
                   return false;
               }
           }, gpuServerUrl).get();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
//        log.info("Binary Message received from client: " + message.getPayload());
        if( gpuSocketSession != null && gpuSocketSession.isOpen()) {
            gpuSocketSession.sendMessage(message);
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("Text Message received from client: " + message.getPayload());
        if( gpuSocketSession != null && gpuSocketSession.isOpen()) {
            gpuSocketSession.sendMessage(message);
        }
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
        log.error("Transport error with client and Java", exception);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Client Connection closed : {}", status);
        if(clientSocketSession != null && gpuSocketSession != null && gpuSocketSession.isOpen()) {
            gpuSocketSession.close(status);
        }

    }

    private void  closeGPUSocket() throws IOException {
        if( gpuSocketSession != null && gpuSocketSession.isOpen()) {
            gpuSocketSession.close();
        }
    }
}
