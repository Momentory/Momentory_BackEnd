package com.example.momentory.domain.roulette.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "roulettes")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Roulette extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rouletteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RouletteType type;

    private String reward;
    private int usedPoint;
    private int earnedPoint;

    private LocalDateTime deadline;  // 룰렛 방문 인증 마감일

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RouletteStatus status = RouletteStatus.IN_PROGRESS;  // 룰렛 상태

    /**
     * 룰렛 인증 완료 시 earnedPoint 업데이트 및 상태 변경
     */
    public void completeRouletteReward(int rewardPoint) {
        this.earnedPoint = rewardPoint;
        this.status = RouletteStatus.SUCCESS;
    }

    /**
     * 마감일 경과로 인한 실패 처리
     */
    public void markAsFailed() {
        this.status = RouletteStatus.FAILED;
    }

    /**
     * 마감일 확인 후 상태 자동 업데이트
     * 조회 시 호출하여 마감일이 지났으면 FAILED로 변경
     */
    public void updateStatusIfExpired() {
        if (this.status == RouletteStatus.IN_PROGRESS &&
            this.deadline != null &&
            LocalDateTime.now().isAfter(this.deadline)) {
            this.status = RouletteStatus.FAILED;
        }
    }
}

