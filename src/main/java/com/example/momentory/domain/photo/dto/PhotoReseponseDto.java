package com.example.momentory.domain.photo.dto;

import com.example.momentory.domain.photo.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.time.LocalDateTime;

public class PhotoReseponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PhotoResponse{
        private Long photoId;
        private String imageName;
        private String imageUrl;
        private Double latitude;
        private Double longitude;
        private String address;
        private String memo;
        private Visibility visibility;
        private LocalDateTime takenAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PhotoUploadResponse{
        private Long photoId;
        private String imageName;
        private String imageUrl;
        
        // 지역 스탬프 관련
        private boolean regionalStampGranted;
        private String regionalStampName;
        
        // 문화시설 관련
        private boolean hasNearbyCulturalSpots;
        private String nearbyCulturalSpotName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class NearbySpotsResponse{
        private Long photoId;
        private Double latitude;
        private Double longitude;
        private String address;
        private java.util.List<SpotInfo> spots;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SpotInfo{
        private String name;
        private String type;
        private String region;
        private String address;
        private String tel;
        private String imageUrl;
    }
}
