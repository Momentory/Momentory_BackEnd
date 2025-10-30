package com.example.momentory.domain.stamp.dto;

import com.example.momentory.domain.stamp.entity.StampType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class StampResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StampInfo {
        private Long stampId;
        private String region;
        private String spotName;
        private StampType type;
        private LocalDateTime issuedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyStampsGrouped {
        private List<StampInfo> regional;
        private List<StampInfo> cultural;
    }
}


