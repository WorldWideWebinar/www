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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MeetingSTTAudioHandler extends AbstractWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(MeetingSTTAudioHandler.class);
    @Value("${gpu.server.url}")
    private String gpuServerUrl;



    // 세션을 관리하기 위한 Map
    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> gpuSessions = new ConcurrentHashMap<>();
    private final Map<String, String> clientGpuSessionKeyMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            clientSessions.put(session.getId(), session); // 클라이언트 세션 저장

            WebSocketSession gpuSession = webSocketClient.execute(new WebSocketHandler() {

                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.info("Connected to GPU server for client session: " + session.getId());
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    log.info("Message received from GPU: " + message.getPayload());
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    log.error("Transport error on GPU session", exception);
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("GPU connection closed for client session: " + session.getId());
                    closeClientSessionWithGpuSessionId(session.getId());
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            }, gpuServerUrl).get();
                gpuSessions.put(session.getId(), gpuSession); // GPU 세션 저장
            clientGpuSessionKeyMap.put(gpuSession.getId(), session.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void closeClientSessionWithGpuSessionId(String id) throws IOException {
        String ClientSessionId = clientGpuSessionKeyMap.remove(id);
        if (ClientSessionId != null) {
            WebSocketSession client = clientSessions.remove(ClientSessionId);
            if (client != null) {
                client.close();
            }
            gpuSessions.remove(ClientSessionId);
        }
    }

    @Override
    protected synchronized void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        WebSocketSession gpuSession = gpuSessions.get(session.getId());
        if (gpuSession != null && gpuSession.isOpen()) {
            synchronized (gpuSession) {
                gpuSession.sendMessage(message);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("Text Message received from client: " + message.getPayload());
        WebSocketSession gpuSession = gpuSessions.get(session.getId());
        if (gpuSession != null && gpuSession.isOpen()) {
            gpuSession.sendMessage(message);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
        log.error("Transport error on client session", exception);
        WebSocketSession gpuSession = gpuSessions.remove(session.getId());
        if (gpuSession != null && gpuSession.isOpen()) {
            gpuSession.close();
        }
        clientSessions.remove(session.getId());
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Client Connection closed: " + session.getId());
        WebSocketSession gpuSession = gpuSessions.remove(session.getId());
        if (gpuSession != null && gpuSession.isOpen()) {
            gpuSession.close(status);
        }
        clientSessions.remove(session.getId());
    }
}
