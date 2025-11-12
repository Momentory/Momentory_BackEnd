package com.example.momentory.domain.notification.event;

import com.example.momentory.domain.notification.dto.NotificationResponseDto;
import com.example.momentory.domain.notification.repository.NotificationSettingRepository;
import com.example.momentory.domain.notification.service.NotificationService;
import com.example.momentory.domain.notification.entity.NotificationSetting;
import com.example.momentory.domain.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationSettingRepository notificationSettingRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            // 알림 설정 확인
            if (!shouldSendNotification(event)) {
                log.info("알림 설정에 의해 알림이 차단되었습니다. userId: {}, type: {}",
                    event.getTargetUser().getId(), event.getType());
                return;
            }

            // 1. DB에 알림 저장
            notificationService.createNotification(
                event.getTargetUser(),
                event.getType(),
                event.getMessage(),
                event.getRelatedId()
            );

            // 2. WebSocket으로 실시간 알림 전송
            sendRealtimeNotification(event);

        } catch (Exception e) {
            log.error("알림 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * WebSocket을 통한 실시간 알림 전송
     */
    private void sendRealtimeNotification(NotificationEvent event) {
        try {
            NotificationResponseDto.WebSocketNotificationMessage message =
                    NotificationResponseDto.WebSocketNotificationMessage.builder()
                            .type(event.getType())
                            .message(event.getMessage())
                            .relatedId(event.getRelatedId())
                            .timestamp(LocalDateTime.now())
                            .build();

            String destination = "/topic/notifications/" + event.getTargetUser().getId();
            messagingTemplate.convertAndSend(destination, message);

            log.info("📤 WebSocket 알림 전송 완료 - userId: {}, type: {}, message: {}",
                    event.getTargetUser().getId(), event.getType(), event.getMessage());
        } catch (Exception e) {
            log.error("❌ WebSocket 알림 전송 실패 - userId: {}, error: {}",
                    event.getTargetUser().getId(), e.getMessage());
        }
    }

    /**
     * 사용자의 알림 설정에 따라 알림을 보낼지 결정
     */
    private boolean shouldSendNotification(NotificationEvent event) {
        NotificationSetting setting = notificationSettingRepository
                .findByUser(event.getTargetUser())
                .orElse(null);

        // 설정이 없으면 기본적으로 알림 전송
        if (setting == null) {
            return true;
        }

        // 모든 알림이 꺼져있으면 전송하지 않음
        if (!setting.isAllNotifications()) {
            return false;
        }

        // 알림 타입에 따라 설정 확인
        return switch (event.getType()) {
            case COMMENT, LIKE -> setting.isCommunityAlert();
            case FOLLOW -> setting.isFollowAlert();
            case LEVEL_UP, ROULETTE, REWARD, ANNOUNCEMENT -> setting.isLevelUpAlert();
        };
    }
}
