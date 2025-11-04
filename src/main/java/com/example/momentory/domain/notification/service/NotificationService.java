package com.example.momentory.domain.notification.service;

import com.example.momentory.domain.notification.converter.NotificationConverter;
import com.example.momentory.domain.notification.dto.NotificationRequestDto;
import com.example.momentory.domain.notification.dto.NotificationResponseDto;
import com.example.momentory.domain.notification.entity.Notification;
import com.example.momentory.domain.notification.entity.NotificationSetting;
import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.notification.repository.NotificationRepository;
import com.example.momentory.domain.notification.repository.NotificationSettingRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserService userService;
    private final NotificationConverter notificationConverter;

    /**
     * 내 알림 조회
     */
    @Transactional(readOnly = true)
    public NotificationResponseDto.NotificationListResponse getMyNotifications() {
        User user = userService.getCurrentUser();

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);
        int unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        return notificationConverter.toNotificationListResponse(notifications, unreadCount);
    }

    /**
     * 미확인 알림 여부 조회
     */
    @Transactional(readOnly = true)
    public NotificationResponseDto.UnreadStatusResponse getUnreadStatus() {
        User user = userService.getCurrentUser();

        boolean hasUnread = notificationRepository.existsByUserAndIsReadFalse(user);
        int unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        return notificationConverter.toUnreadStatusResponse(hasUnread, unreadCount);
    }

    /**
     * 전체 알림 확인 처리
     */
    @Transactional
    public void markAllAsRead() {
        User user = userService.getCurrentUser();

        notificationRepository.markAllAsReadByUser(user);
    }

    /**
     * 알림 하나 확인 처리
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        User user = userService.getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }

        notification.markAsRead();
    }

    /**
     * 리스트 알림 확인 처리
     */
    @Transactional
    public void markListAsRead(NotificationRequestDto.ReadNotificationsRequest request) {
        User user = userService.getCurrentUser();

        notificationRepository.markAsReadByIds(request.getNotificationIds(), user);
    }

    /**
     * 알림 생성 (이벤트 리스너에서 호출)
     */
    @Transactional
    public void createNotification(User user, NotificationType type, String message, Long relatedId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .relatedId(relatedId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 알림 설정 조회
     */
    @Transactional
    public NotificationResponseDto.NotificationSettingResponse getNotificationSettings() {
        User user = userService.getCurrentUser();

        NotificationSetting setting = notificationSettingRepository.findByUser(user)
                .orElseGet(() -> {
                    // 설정이 없으면 기본값으로 생성
                    NotificationSetting newSetting = NotificationSetting.builder()
                            .user(user)
                            .allNotifications(true)
                            .communityAlert(true)
                            .followAlert(true)
                            .levelUpAlert(true)
                            .build();
                    return notificationSettingRepository.save(newSetting);
                });

        return NotificationResponseDto.NotificationSettingResponse.builder()
                .allNotifications(setting.isAllNotifications())
                .communityAlert(setting.isCommunityAlert())
                .followAlert(setting.isFollowAlert())
                .levelUpAlert(setting.isLevelUpAlert())
                .build();
    }

    /**
     * 알림 설정 업데이트
     */
    @Transactional
    public NotificationResponseDto.NotificationSettingResponse updateNotificationSettings(
            NotificationRequestDto.UpdateNotificationSettingRequest request) {
        User user = userService.getCurrentUser();

        NotificationSetting setting = notificationSettingRepository.findByUser(user)
                .orElseGet(() -> NotificationSetting.builder()
                        .user(user)
                        .allNotifications(true)
                        .communityAlert(true)
                        .followAlert(true)
                        .levelUpAlert(true)
                        .build());

        setting.updateSettings(
                request.isAllNotifications(),
                request.isCommunityAlert(),
                request.isFollowAlert(),
                request.isLevelUpAlert()
        );

        notificationSettingRepository.save(setting);

        return NotificationResponseDto.NotificationSettingResponse.builder()
                .allNotifications(setting.isAllNotifications())
                .communityAlert(setting.isCommunityAlert())
                .followAlert(setting.isFollowAlert())
                .levelUpAlert(setting.isLevelUpAlert())
                .build();
    }
}