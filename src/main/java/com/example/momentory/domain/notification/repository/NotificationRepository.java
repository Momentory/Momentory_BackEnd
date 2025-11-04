package com.example.momentory.domain.notification.repository;

import com.example.momentory.domain.notification.entity.Notification;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 모든 알림 조회 (최신순)
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    // 특정 사용자의 읽지 않은 알림 개수 조회
    int countByUserAndIsReadFalse(User user);

    // 특정 사용자의 읽지 않은 알림이 있는지 확인
    boolean existsByUserAndIsReadFalse(User user);

    // 특정 사용자의 모든 알림을 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    // 특정 알림 ID 목록을 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId IN :notificationIds AND n.user = :user")
    void markAsReadByIds(@Param("notificationIds") List<Long> notificationIds, @Param("user") User user);
}