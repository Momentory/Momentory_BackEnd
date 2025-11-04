package com.example.momentory.domain.point.dto;

import com.example.momentory.domain.point.entity.PointActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PointResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPoint{
        private int currentPoint;
        private int totalPoint;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterPoint{
        private int level;
        private UserPoint userPoint;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointHistory{
        List<PointInfo> points;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointInfo {
        private int amount;
        private PointActionType action;    // 영문 코드
        private String actionDesc;         // 한글 설명
        private LocalDateTime createdAt;
    }



}
