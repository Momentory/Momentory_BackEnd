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
}