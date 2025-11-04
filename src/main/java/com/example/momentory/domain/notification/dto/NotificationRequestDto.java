package com.example.momentory.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class NotificationRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadNotificationsRequest {
        private List<Long> notificationIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateNotificationSettingRequest {
        private boolean allNotifications;   // 모든 알림
        private boolean communityAlert;     // 커뮤니티 알림
        private boolean followAlert;        // 팔로우 알림
        private boolean levelUpAlert;       // 캐릭터 레벨업 알림
    }
}