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

    private boolean isCompleted;  // 인증 완료 여부

    /**
     * 룰렛 인증 완료 시 earnedPoint 업데이트
     */
    public void completeRouletteReward(int rewardPoint) {
        this.earnedPoint = rewardPoint;
        this.isCompleted = true;
    }
}

