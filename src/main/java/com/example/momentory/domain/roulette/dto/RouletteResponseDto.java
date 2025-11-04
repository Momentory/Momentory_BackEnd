package com.example.momentory.domain.roulette.dto;

import com.example.momentory.domain.roulette.entity.RouletteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RouletteResponseDto {

    /**
     * 방문하지 않은 지역 5개 랜덤 조회 응답
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UnvisitedRegions {
        private List<String> regions;  // 방문하지 않은 지역명 리스트 (최대 5개)
    }

    /**
     * 룰렛 스핀 결과 응답
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class SpinResult {
        private Long rouletteId;
        private String reward;          // 선택된 지역명
        private int usedPoint;          // 차감된 포인트 (200)
        private int remainingPoint;     // 남은 포인트
        private LocalDateTime deadline; // 인증 마감일 (3일 후)
    }

    /**
     * 룰렛 내역 조회 응답
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RouletteHistory {
        private List<RouletteInfo> roulettes;
    }

    /**
     * 룰렛 상세 정보
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RouletteInfo {
        private Long rouletteId;
        private RouletteType type;
        private String reward;          // 미션 지역
        private int usedPoint;          // 사용한 포인트
        private int earnedPoint;        // 획득한 포인트
        private boolean isCompleted;    // 인증 완료 여부
        private LocalDateTime createdAt;
        private LocalDateTime deadline; // 인증 마감일
    }

    /**
     * 미완료 룰렛 목록 조회 응답
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class IncompleteRoulettes {
        private List<RouletteInfo> roulettes;
    }
}

