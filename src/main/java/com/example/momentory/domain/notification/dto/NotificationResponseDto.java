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
}