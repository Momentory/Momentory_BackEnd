package com.example.momentory.domain.roulette.dto;

import com.example.momentory.domain.roulette.entity.RouletteSlotType;
import com.example.momentory.domain.roulette.entity.RouletteStatus;
import com.example.momentory.domain.roulette.entity.RouletteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RouletteResponseDto {

    /**
     * 룰렛 슬롯 8개 랜덤 조회 응답 (지역 + 아이템)
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RouletteSlots {
        private List<RouletteSlot> slots;  // 룰렛 슬롯 리스트 (최대 8개)
    }

    /**
     * 룰렛 슬롯 정보 (지역 또는 아이템)
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RouletteSlot {
        private RouletteSlotType type;  // REGION 또는 ITEM
        private String name;            // 지역명 또는 아이템명
        private String imageUrl;        // 지역 이미지 URL 또는 아이템 이미지 URL
        private Long itemId;            // 아이템인 경우 아이템 ID (지역인 경우 null)
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
        private String reward;          // 미션 지역 또는 아이템
        private int usedPoint;          // 사용한 포인트
        private int earnedPoint;        // 획득한 포인트
        private RouletteStatus status;  // 룰렛 상태 (IN_PROGRESS, SUCCESS, FAILED)
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

