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

    private boolean commentAlert;
    private boolean followAlert;
    private boolean growthAlert;
}

