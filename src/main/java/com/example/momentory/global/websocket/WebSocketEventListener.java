package com.example.momentory.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

/**
 * WebSocket 연결/해제 이벤트 처리
 * - 연결/해제 로그 기록
 * - 구독/구독 취소 이벤트 처리
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    /**
     * WebSocket 연결 성공 시 호출
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("🔌 WebSocket CONNECTED - sessionId: {}, userId: {}",
                    sessionId, user.getName());
        } else {
            log.info("🔌 WebSocket CONNECTED - sessionId: {} (anonymous)", sessionId);
        }
    }

    /**
     * WebSocket 연결 해제 시 호출
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("🔌 WebSocket DISCONNECTED - sessionId: {}, userId: {}",
                    sessionId, user.getName());
        } else {
            log.info("🔌 WebSocket DISCONNECTED - sessionId: {}", sessionId);
        }
    }

    /**
     * 구독 시 호출
     */
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("📬 SUBSCRIBED - sessionId: {}, userId: {}, destination: {}",
                    sessionId, user.getName(), destination);
        } else {
            log.info("📬 SUBSCRIBED - sessionId: {}, destination: {}",
                    sessionId, destination);
        }
    }

    /**
     * 구독 취소 시 호출
     */
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("📭 UNSUBSCRIBED - sessionId: {}, userId: {}",
                    sessionId, user.getName());
        } else {
            log.info("📭 UNSUBSCRIBED - sessionId: {}", sessionId);
        }
    }
}