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
        private Double latitude;   // 위도
        private Double longitude;   // 경도
        private String address;
        private String memo;
        private Visibility visibility;
        private LocalDateTime createdAt;
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
        private String regionName;  // 지역명 (예: "부천시")
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

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class LocationToAddressResponse{
        private Double latitude;
        private Double longitude;
        private String address;
        private String cityName; // "부천시" 형태로 추출된 도시명
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class MyPhotosCursorResponse{
        private java.util.List<PhotoResponse> photos;
        private LocalDateTime nextCursor;  // 다음 페이지 커서 (마지막 항목의 createdAt)
        private Boolean hasNext;            // 다음 페이지 존재 여부
    }
}
