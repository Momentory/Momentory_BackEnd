package com.example.momentory.domain.notification.converter;

import com.example.momentory.domain.notification.dto.NotificationResponseDto;
import com.example.momentory.domain.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationConverter {

    public NotificationResponseDto.NotificationDto toNotificationDto(Notification notification) {
        return NotificationResponseDto.NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType())
                .message(notification.getMessage())
                .relatedId(notification.getRelatedId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public NotificationResponseDto.NotificationListResponse toNotificationListResponse(
            List<Notification> notifications, int unreadCount) {
        List<NotificationResponseDto.NotificationDto> notificationDtos = notifications.stream()
                .map(this::toNotificationDto)
                .collect(Collectors.toList());

        return NotificationResponseDto.NotificationListResponse.builder()
                .notifications(notificationDtos)
                .totalCount(notifications.size())
                .unreadCount(unreadCount)
                .build();
    }

    public NotificationResponseDto.UnreadStatusResponse toUnreadStatusResponse(boolean hasUnread, int unreadCount) {
        return NotificationResponseDto.UnreadStatusResponse.builder()
                .hasUnread(hasUnread)
                .unreadCount(unreadCount)
                .build();
    }
}