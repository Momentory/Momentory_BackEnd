package com.example.momentory.domain.character.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LevelCalculator 단위 테스트")
class LevelCalculatorTest {

    private final LevelCalculator levelCalculator = new LevelCalculator();

    @Test
    @DisplayName("누적 포인트로 레벨 계산 - 정상 케이스")
    void calculateLevel_success() {
        // 레벨 1 구간 (0~500)
        assertEquals(1, levelCalculator.calculateLevel(0));
        assertEquals(1, levelCalculator.calculateLevel(250));
        assertEquals(1, levelCalculator.calculateLevel(500));
        
        // 레벨 2 (600)
        assertEquals(1, levelCalculator.calculateLevel(599));  // 아직 레벨 2 안 됨
        assertEquals(2, levelCalculator.calculateLevel(600));  // 레벨 2 달성
        assertEquals(2, levelCalculator.calculateLevel(700));
        
        // 레벨 3 (800)
        assertEquals(2, levelCalculator.calculateLevel(799));
        assertEquals(3, levelCalculator.calculateLevel(800));
        assertEquals(3, levelCalculator.calculateLevel(900));
        
        // 레벨 4 (1100)
        assertEquals(3, levelCalculator.calculateLevel(1099));
        assertEquals(4, levelCalculator.calculateLevel(1100));
        assertEquals(4, levelCalculator.calculateLevel(1200));
        
        // 레벨 5 (1500)
        assertEquals(4, levelCalculator.calculateLevel(1499));
        assertEquals(5, levelCalculator.calculateLevel(1500));
        assertEquals(5, levelCalculator.calculateLevel(1600));
        
        // 레벨 6 (2000 = 1500 + 500)
        assertEquals(5, levelCalculator.calculateLevel(1999));
        assertEquals(6, levelCalculator.calculateLevel(2000));
    }

    @Test
    @DisplayName("레벨에 도달하는 데 필요한 누적 포인트 계산")
    void getRequiredPointsForLevel_success() {
        assertEquals(500, levelCalculator.getRequiredPointsForLevel(1));   // 레벨 1: 500p
        assertEquals(600, levelCalculator.getRequiredPointsForLevel(2));   // 레벨 2: 600p
        assertEquals(800, levelCalculator.getRequiredPointsForLevel(3));   // 레벨 3: 800p
        assertEquals(1100, levelCalculator.getRequiredPointsForLevel(4));  // 레벨 4: 1100p
        assertEquals(1500, levelCalculator.getRequiredPointsForLevel(5));  // 레벨 5: 1500p
        assertEquals(2000, levelCalculator.getRequiredPointsForLevel(6));  // 레벨 6: 2000p
        assertEquals(2600, levelCalculator.getRequiredPointsForLevel(7));  // 레벨 7: 2600p
    }

    @Test
    @DisplayName("현재 레벨에서 다음 레벨까지 필요한 포인트")
    void getPointsForNextLevel_success() {
        assertEquals(100, levelCalculator.getPointsForNextLevel(1));  // 1→2: 100p
        assertEquals(200, levelCalculator.getPointsForNextLevel(2));  // 2→3: 200p
        assertEquals(300, levelCalculator.getPointsForNextLevel(3));  // 3→4: 300p
        assertEquals(400, levelCalculator.getPointsForNextLevel(4));  // 4→5: 400p
        assertEquals(500, levelCalculator.getPointsForNextLevel(5));  // 5→6: 500p
    }

    @Test
    @DisplayName("다음 레벨까지 남은 포인트 계산")
    void getPointsUntilNextLevel_success() {
        // 레벨 1 (500p 구간)
        assertEquals(100, levelCalculator.getPointsUntilNextLevel(500));  // 레벨 2까지 100p 필요
        assertEquals(50, levelCalculator.getPointsUntilNextLevel(550));   // 레벨 2까지 50p 필요
        
        // 레벨 2 (600p)
        assertEquals(200, levelCalculator.getPointsUntilNextLevel(600));  // 레벨 3까지 200p 필요
        assertEquals(100, levelCalculator.getPointsUntilNextLevel(700));  // 레벨 3까지 100p 필요
        
        // 레벨 3 (800p)
        assertEquals(300, levelCalculator.getPointsUntilNextLevel(800));  // 레벨 4까지 300p 필요
        assertEquals(100, levelCalculator.getPointsUntilNextLevel(1000)); // 레벨 4까지 100p 필요
        
        // 레벨 4 (1100p)
        assertEquals(400, levelCalculator.getPointsUntilNextLevel(1100)); // 레벨 5까지 400p 필요
        
        // 레벨 5 (1500p)
        assertEquals(500, levelCalculator.getPointsUntilNextLevel(1500)); // 레벨 6까지 500p 필요
    }

    @Test
    @DisplayName("가입 시나리오 - 회원가입 보상 500p 받고 레벨 1")
    void signupScenario() {
        int signupBonus = 500;
        assertEquals(1, levelCalculator.calculateLevel(signupBonus));
        assertEquals(100, levelCalculator.getPointsUntilNextLevel(signupBonus));  // 레벨 2까지 100p 필요
    }

    @Test
    @DisplayName("실전 시나리오 - 사진 업로드로 레벨업")
    void photoUploadScenario() {
        int totalPoints = 500;  // 가입 보상
        assertEquals(1, levelCalculator.calculateLevel(totalPoints));
        
        // 사진 2장 업로드 (+100p)
        totalPoints += 100;  // 600p
        assertEquals(2, levelCalculator.calculateLevel(totalPoints));
        assertEquals(200, levelCalculator.getPointsUntilNextLevel(totalPoints));  // 레벨 3까지 200p 필요
        
        // 사진 4장 더 업로드 (+200p)
        totalPoints += 200;  // 800p
        assertEquals(3, levelCalculator.calculateLevel(totalPoints));
        assertEquals(300, levelCalculator.getPointsUntilNextLevel(totalPoints));  // 레벨 4까지 300p 필요
    }

    @Test
    @DisplayName("실전 시나리오 - 룰렛 인증으로 레벨업")
    void rouletteScenario() {
        int totalPoints = 500;  // 가입 보상, 레벨 1
        
        // 룰렛 실행 (-200p는 누적 포인트에 영향 없음)
        // 룰렛 인증 성공 (+500p)
        totalPoints += 500;  // 1000p
        assertEquals(3, levelCalculator.calculateLevel(totalPoints));  // 레벨 3
        
        // 레벨업 보상 (+200p)
        totalPoints += 200;  // 1200p
        assertEquals(4, levelCalculator.calculateLevel(totalPoints));  // 레벨 4
    }
}

