package com.example.momentory.domain.notification.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "notification_settings")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "all_notifications")
    @Builder.Default
    private boolean allNotifications = true;;  // 모든 알림 on/off

    @Column(name = "community_alert")
    @Builder.Default
    private boolean communityAlert = true;;  // 커뮤니티 알림 (댓글 등)

    @Column(name = "follow_alert")
    @Builder.Default
    private boolean followAlert = true;;  // 팔로우 알림

    @Column(name = "level_up_alert")
    @Builder.Default
    private boolean levelUpAlert = true;;  // 캐릭터 레벨업 알림

    // 알림 설정 업데이트 메서드
    public void updateSettings(boolean allNotifications, boolean communityAlert,
                               boolean followAlert, boolean levelUpAlert) {
        this.allNotifications = allNotifications;
        this.communityAlert = communityAlert;
        this.followAlert = followAlert;
        this.levelUpAlert = levelUpAlert;
    }
}

