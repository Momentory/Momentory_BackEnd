package com.example.momentory.domain.character.entity;

import com.example.momentory.domain.user.entity.User;
import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 레벨업 이력을 기록하여 중복 보상 지급을 방지합니다.
 * 사용자-레벨 조합으로 유니크 제약 조건을 설정하여
 * 캐릭터가 여러 개여도 사용자당 한 번만 보상을 지급합니다.
 */
@Entity
@Table(
    name = "level_up_histories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_level",
            columnNames = {"user_id", "reached_level"}
        )
    }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LevelUpHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    /**
     * 도달한 레벨 (2, 3, 4, ...)
     */
    @Column(name = "reached_level", nullable = false)
    private int reachedLevel;

    /**
     * 레벨업 시 지급된 보너스 포인트
     */
    @Column(name = "bonus_points", nullable = false)
    private int bonusPoints;

    /**
     * 레벨업 당시의 누적 포인트
     */
    @Column(name = "total_points_at_levelup")
    private int totalPointsAtLevelup;
}