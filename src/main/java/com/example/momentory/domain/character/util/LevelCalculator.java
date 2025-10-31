package com.example.momentory.domain.character.util;

import org.springframework.stereotype.Component;

@Component
public class LevelCalculator {

    /**
     * 누적 포인트를 기준으로 비선형적으로 레벨을 계산합니다.
     * 예시:
     * 1→2 : 100p 필요
     * 2→3 : 추가 200p 필요
     * 3→4 : 추가 300p 필요
     *
     * @param totalPoints 누적 포인트
     * @return 계산된 레벨
     */
    public int calculateLevel(int totalPoints) {
        if (totalPoints <= 0) return 1;

        int level = 1;
        int requiredForNextLevel = 100;
        int accumulated = 0;

        while (totalPoints >= accumulated + requiredForNextLevel) {
            accumulated += requiredForNextLevel;
            level++;
            requiredForNextLevel += 100; // 다음 레벨로 갈수록 100씩 더 필요
        }

        return level;
    }

    /**
     * 특정 레벨에 도달하는 데 필요한 누적 포인트를 계산합니다.
     * 1레벨→2레벨: 100
     * 2레벨→3레벨: 100 + 200 = 300
     * 3레벨→4레벨: 100 + 200 + 300 = 600 ...
     */
    public int getRequiredPointsForLevel(int level) {
        if (level <= 1) return 0;
        return (level - 1) * level * 50; // 등차수열 합 공식: n(n-1)/2 * 100 → 단순화
    }
}
