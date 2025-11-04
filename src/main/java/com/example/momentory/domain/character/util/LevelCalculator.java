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
        if (totalPoints <= 500) return 1; // 500p까지는 무조건 레벨 1 유지

        int level = 1;
        int requiredForNextLevel = 100;
        int accumulated = 500; // 500포인트를 기본 구간으로 설정

        while (totalPoints >= accumulated + requiredForNextLevel) {
            accumulated += requiredForNextLevel;
            level++;
            requiredForNextLevel += 100; // 다음 레벨로 갈수록 100씩 증가
        }

        return level;
    }


    /**
     * 특정 레벨에 도달하는 데 필요한 누적 포인트를 계산합니다.
     * 레벨 1: 500p (가입 보상 기본 구간)
     * 레벨 2: 600p (500 + 100)
     * 레벨 3: 800p (500 + 100 + 200)
     * 레벨 4: 1100p (500 + 100 + 200 + 300)
     * 레벨 5: 1500p (500 + 100 + 200 + 300 + 400)
     * 
     * 공식: 500 + 100 × (1 + 2 + ... + (level-1))
     *      = 500 + 100 × (level-1) × level ÷ 2
     */
    public int getRequiredPointsForLevel(int level) {
        if (level <= 1) return 500;  // 레벨 1은 500p (가입 보상 포함)
        return 500 + 100 * (level - 1) * level / 2;  // 등차수열 합 공식 적용
    }

    /**
     * 현재 레벨에서 다음 레벨까지 필요한 포인트를 계산합니다.
     * 레벨 1→2: 100p
     * 레벨 2→3: 200p
     * 레벨 3→4: 300p
     */
    public int getPointsForNextLevel(int currentLevel) {
        if (currentLevel <= 0) return 100;
        return currentLevel * 100;
    }

    /**
     * 현재 누적 포인트로 다음 레벨까지 남은 포인트를 계산합니다.
     */
    public int getPointsUntilNextLevel(int totalPoints) {
        int currentLevel = calculateLevel(totalPoints);
        int nextLevelPoints = getRequiredPointsForLevel(currentLevel + 1);
        return nextLevelPoints - totalPoints;
    }
}
