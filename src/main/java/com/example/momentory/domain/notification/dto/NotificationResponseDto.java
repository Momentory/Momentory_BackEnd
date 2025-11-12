package com.example.momentory.domain.notification.dto;

import com.example.momentory.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDto {
        private Long notificationId;
        private NotificationType type;
        private String message;
        private Long relatedId;
        private String relatedUrl;
        private boolean isRead;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListResponse {
        private List<NotificationDto> notifications;
        private int totalCount;
        private int unreadCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnreadStatusResponse {
        private boolean hasUnread;
        private int unreadCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettingResponse {
        private boolean allNotifications;   // 모든 알림
        private boolean communityAlert;     // 커뮤니티 알림
        private boolean followAlert;        // 팔로우 알림
        private boolean levelUpAlert;       // 캐릭터 레벨업 알림
    }

    /**
     * WebSocket 실시간 알림 메시지
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebSocketNotificationMessage {
        private NotificationType type;      // 알림 타입
        private String message;             // 알림 메시지
        private Long relatedId;             // 관련 엔티티 ID
        private LocalDateTime timestamp;    // 전송 시각
    }
}