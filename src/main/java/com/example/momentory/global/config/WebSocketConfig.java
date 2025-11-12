package com.example.momentory.global.config;

import com.example.momentory.global.websocket.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정
 * - STOMP 프로토콜 사용
 * - SockJS 폴백 지원
 * - JWT 인증 연동
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Log4j2
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * 메시지 브로커 설정
     * - /topic : 1:N 브로드캐스트 (예: 공지사항)
     * - /queue : 1:1 개인 메시지 (예: 개별 알림)
     * - /app : 클라이언트 → 서버 메시지 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Simple broker 사용 (프로덕션에서는 RabbitMQ/ActiveMQ 권장)
        config.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 서버로 메시지 보낼 때 prefix
        config.setApplicationDestinationPrefixes("/app");

        // 특정 사용자에게 메시지 보낼 때 사용할 prefix
        config.setUserDestinationPrefix("/user");

        log.info("✅ Message Broker configured - /topic, /queue, /app");
    }

    /**
     * STOMP 엔드포인트 등록
     * - /ws : WebSocket 연결 엔드포인트
     * - SockJS 폴백 활성화 (WebSocket 미지원 브라우저 대응)
     * - CORS 허용 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "https://www.momentory.store",
                        "http://www.momentory.store",
                        "https://momentory.store",
                        "https://momentoryy.vercel.app"
                )
                .withSockJS();  // SockJS 폴백 활성화

        log.info("✅ STOMP Endpoint registered - /ws (with SockJS)");
    }

    /**
     * 클라이언트 인바운드 채널 설정
     * - JWT 인증 인터셉터 등록
     * - 연결 시 토큰 검증
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
        log.info("✅ WebSocket Auth Interceptor registered");
    }
}