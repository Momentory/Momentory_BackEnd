package com.example.momentory.domain.roulette.dto;

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
        private String selectedRegion;  // 룰렛에서 선택된 지역명
    }
}

