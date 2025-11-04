package com.example.momentory.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class HomeDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelSpotSummary {
        private String name;
        private String type;
        private String region;
        private String address;
        private String tel;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPhotoSummary {
        private Long photoId;
        private String imageUrl;
        private String address;
        private String ownerNickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventSummary {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String region;
        private String imageUrl;
    }
}


