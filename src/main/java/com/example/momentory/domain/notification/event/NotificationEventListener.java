package com.example.momentory.domain.notification.event;

import com.example.momentory.domain.notification.dto.NotificationResponseDto;
import com.example.momentory.domain.notification.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 알림 생성 (DB 저장)
     */
    @Async
    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            // 알림 설정 확인
            if (!shouldSendNotification(event)) {
                return;
            }

            // DB에 알림 저장
            notificationService.createNotification(
                event.getTargetUser(),
                event.getType(),
                event.getMessage(),
                event.getRelatedId()
            );

            // 트랜잭션 커밋 후 WebSocket 전송은 별도로 처리됨 (아래 메서드)

        } catch (Exception e) {
            log.error("알림 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 트랜잭션 커밋 후 WebSocket으로 실시간 알림 전송
     * @TransactionalEventListener를 사용하여 DB 커밋 후 실행
     */
    @Async
    @org.springframework.transaction.event.TransactionalEventListener(phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    public void sendWebSocketNotificationAfterCommit(NotificationEvent event) {
        try {
            // 알림 설정 확인
            if (!shouldSendNotification(event)) {
                return;
            }

            // 트랜잭션 커밋 후 최신 미확인 알림 개수 조회
            int unreadCount = notificationRepository.countByUserAndIsReadFalse(event.getTargetUser());

            NotificationResponseDto.WebSocketNotificationMessage message =
                    NotificationResponseDto.WebSocketNotificationMessage.builder()
                            .type(event.getType())
                            .message(event.getMessage())
                            .relatedId(event.getRelatedId())
                            .timestamp(LocalDateTime.now())
                            .unreadCount(unreadCount)  // 정확한 미확인 알림 개수
                            .build();

            String destination = "/topic/notifications/" + event.getTargetUser().getId();
            messagingTemplate.convertAndSend(destination, message);

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
