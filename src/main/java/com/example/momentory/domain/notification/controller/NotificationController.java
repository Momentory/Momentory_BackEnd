package com.example.momentory.domain.notification.controller;

import com.example.momentory.domain.notification.dto.NotificationRequestDto;
import com.example.momentory.domain.notification.dto.NotificationResponseDto;
import com.example.momentory.domain.notification.service.NotificationService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "내 알림 조회", description = "로그인한 사용자의 모든 알림을 조회합니다.")
    public ApiResponse<NotificationResponseDto.NotificationListResponse> getMyNotifications() {
        return ApiResponse.onSuccess(notificationService.getMyNotifications());
    }

    @GetMapping("/unread")
    @Operation(summary = "미확인 알림 여부", description = "읽지 않은 알림이 있는지 확인합니다.")
    public ApiResponse<NotificationResponseDto.UnreadStatusResponse> getUnreadStatus() {
        return ApiResponse.onSuccess(notificationService.getUnreadStatus());
    }

    @PutMapping("/read-all")
    @Operation(summary = "전체 알림 확인 처리", description = "모든 알림을 읽음 처리합니다.")
    public ApiResponse<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.onSuccess("모든 알림이 읽음 처리되었습니다.");
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 하나 확인 처리", description = "특정 알림을 읽음 처리합니다.")
    public ApiResponse<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.onSuccess("알림이 읽음 처리되었습니다.");
    }

    @PutMapping("/read")
    @Operation(summary = "리스트 알림 확인 처리", description = "여러 개의 알림을 한 번에 읽음 처리합니다.")
    public ApiResponse<String> markListAsRead(@RequestBody NotificationRequestDto.ReadNotificationsRequest request) {
        notificationService.markListAsRead(request);
        return ApiResponse.onSuccess("선택한 알림들이 읽음 처리되었습니다.");
    }

    @GetMapping("/settings")
    @Operation(summary = "알림 설정 조회", description = "사용자의 알림 설정을 조회합니다.")
    public ApiResponse<NotificationResponseDto.NotificationSettingResponse> getNotificationSettings() {
        return ApiResponse.onSuccess(notificationService.getNotificationSettings());
    }

    @PutMapping("/settings/me")
    @Operation(summary = "알림 설정 변경", description = "사용자의 알림 설정을 변경합니다.")
    public ApiResponse<NotificationResponseDto.NotificationSettingResponse> updateNotificationSettings(
            @RequestBody NotificationRequestDto.UpdateNotificationSettingRequest request) {
        return ApiResponse.onSuccess(notificationService.updateNotificationSettings(request));
    }
}