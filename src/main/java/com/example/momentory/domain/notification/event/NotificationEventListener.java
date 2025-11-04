package com.example.momentory.domain.notification.event;

import com.example.momentory.domain.notification.repository.NotificationSettingRepository;
import com.example.momentory.domain.notification.service.NotificationService;
import com.example.momentory.domain.notification.entity.NotificationSetting;
import com.example.momentory.domain.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationSettingRepository notificationSettingRepository;

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

            // 알림 생성
            notificationService.createNotification(
                event.getTargetUser(),
                event.getType(),
                event.getMessage(),
                event.getRelatedId()
            );

        } catch (Exception e) {
            log.error("알림 생성 중 오류 발생: {}", e.getMessage(), e);
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
            case COMMENT -> setting.isCommunityAlert();
            case FOLLOW -> setting.isFollowAlert();
            case LEVEL_UP, ROULETTE, REWARD -> setting.isLevelUpAlert();
        };
    }
}
