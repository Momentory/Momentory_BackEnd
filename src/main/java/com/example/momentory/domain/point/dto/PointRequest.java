package com.example.momentory.domain.point.dto;

import com.example.momentory.domain.point.entity.PointActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Add{
        private Long userId;
        private int amount;
        private PointActionType action;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Use{
        private Long userId;
        private int amount;
        private PointActionType action;
    }
}
