package com.example.momentory.domain.notification.scheduler;

import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.notification.event.NotificationEvent;
import com.example.momentory.domain.roulette.entity.Roulette;
import com.example.momentory.domain.roulette.repository.RouletteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RouletteRepository rouletteRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 매일 오전 10시에 실행되어 내일 마감되는 룰렛을 확인하고 알림을 발송합니다.
     */
    @Scheduled(cron = "0 0 10 * * *")  // 매일 오전 10시
    public void sendRouletteDeadlineNotifications() {

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        // 내일 마감되는 진행 중인 룰렛 조회
        LocalDateTime startOfTomorrow = tomorrow.toLocalDate().atStartOfDay();
        LocalDateTime endOfTomorrow = tomorrow.toLocalDate().atTime(23, 59, 59);

        List<Roulette> roulettes = rouletteRepository.findAll().stream()
                .filter(r -> r.getStatus() == com.example.momentory.domain.roulette.entity.RouletteStatus.IN_PROGRESS)
                .filter(r -> r.getDeadline() != null)
                .filter(r -> !r.getDeadline().isBefore(startOfTomorrow) && !r.getDeadline().isAfter(endOfTomorrow))
                .toList();


        // 각 룰렛에 대해 알림 발송
        for (Roulette roulette : roulettes) {
            try {
                NotificationEvent event = NotificationEvent.builder()
                        .targetUser(roulette.getUser())
                        .type(NotificationType.ROULETTE)
                        .message("룰렛 방문 인증 마감이 내일까지입니다! 잊지 말고 인증해주세요.")
                        .relatedId(roulette.getRouletteId())
                        .build();
                eventPublisher.publishEvent(event);
            } catch (Exception e) {
                log.error("❌ 룰렛 마감 알림 전송 실패 - rouletteId: {}, error: {}",
                        roulette.getRouletteId(), e.getMessage());
            }
        }
    }
}