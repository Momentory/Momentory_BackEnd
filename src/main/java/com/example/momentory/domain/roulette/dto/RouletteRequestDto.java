package com.example.momentory.domain.roulette.dto;

import com.example.momentory.domain.roulette.entity.RouletteSlotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RouletteRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpinRoulette {
        private RouletteSlotType type;  // REGION 또는 ITEM
        private String selectedName;    // 선택된 지역명 또는 아이템명
        private Long itemId;            // 아이템인 경우 아이템 ID (지역인 경우 null)
    }
}

