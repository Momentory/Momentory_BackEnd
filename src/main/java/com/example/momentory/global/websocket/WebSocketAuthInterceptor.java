package com.example.momentory.global.websocket;

import com.example.momentory.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebSocket 연결 시 JWT 인증 처리
 * - STOMP CONNECT 메시지에서 Authorization 헤더 추출
 * - JWT 토큰 검증 및 사용자 인증 정보 설정
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더에서 토큰 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);

                // "Bearer " 접두사 제거
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                try {
                    // 토큰 검증 및 사용자 정보 추출
                    if (tokenProvider.validateToken(token)) {
                        Long userId = tokenProvider.extractUserIdFromToken(token);

                        // 인증 정보 설정
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userId,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                );

                        accessor.setUser(authentication);
                    } else {
                        log.warn("⚠️ Invalid WebSocket token");
                    }
                } catch (Exception e) {
                    log.error("❌ WebSocket authentication failed: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ No Authorization header in WebSocket CONNECT");
            }
        }

        return message;
    }
}