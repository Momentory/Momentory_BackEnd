package com.example.momentory.domain.notification.event;

import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationEvent {
    private User targetUser;        // 알림을 받을 사용자
    private NotificationType type;  // 알림 타입
    private String message;         // 알림 메시지
    private Long relatedId;         // 관련 ID (댓글ID, 게시글ID 등)
}