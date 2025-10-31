package com.example.momentory.domain.roulette.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

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

    /**
     * 룰렛 인증 완료 시 earnedPoint 업데이트
     */
    public void completeRouletteReward(int rewardPoint) {
        this.earnedPoint = rewardPoint;
    }
}

