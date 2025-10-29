package com.example.momentory.domain.photo.dto;

import com.example.momentory.domain.photo.entity.StampType;
import lombok.Getter;
import lombok.Setter;

public class StampRequestDto {

    @Getter
    @Setter
    public static class CulturalStampSimpleRequest {
        private String spotName;  // 프론트에서 전달받는 유일한 값
    }
}
