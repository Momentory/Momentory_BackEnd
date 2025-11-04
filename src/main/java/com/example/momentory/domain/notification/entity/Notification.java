package com.example.momentory.domain.notification.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private Long relatedId;  // 댓글ID, 게시글ID, 팔로우ID 등

    @Column(name = "is_read")
    private boolean isRead;

    // 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}

