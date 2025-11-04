package com.example.momentory.domain.notification.repository;

import com.example.momentory.domain.notification.entity.NotificationSetting;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    // 특정 사용자의 알림 설정 조회
    Optional<NotificationSetting> findByUser(User user);
}