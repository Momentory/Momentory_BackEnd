package com.example.momentory.domain.character.util;

import org.springframework.stereotype.Component;

@Component
public class LevelCalculator {

    /**
     * 누적 포인트를 기준으로 레벨을 계산합니다.
     * 레벨 공식: Level = 1 + (누적 포인트 / 100)
     * 
     * @param totalPoints 누적 포인트 (PointHistory의 양수 합계)
     * @return 계산된 레벨 (최소 1)
     */
    public int calculateLevel(int totalPoints) {
        if (totalPoints < 0) {
            return 1;
        }
        // 레벨 1 시작, 100포인트당 1레벨 증가
        return Math.max(1, 1 + (totalPoints / 100));
    }

    /**
     * 특정 레벨에 도달하는데 필요한 누적 포인트를 계산합니다.
     * 
     * @param level 목표 레벨
     * @return 필요한 누적 포인트
     */
    public int getRequiredPointsForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return (level - 1) * 100;
    }
}

