package com.example.momentory.domain.photo.dto;

import com.example.momentory.domain.photo.entity.Visibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PhotoRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PhotoUpload{
        @NotNull
        private String imageName;
        @NotNull
        private String imageUrl;
        private Double latitude;   // 위도 (업로드 시에만 설정)
        private Double longitude;  // 경도 (업로드 시에만 설정)
        private String cityName;
        private String color;  // 지역별 색깔 (예: "#FFB7B7")
        private Boolean visibility;
        private String memo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PhotoUpdate{
        private String address;
        private String memo;
        private Boolean visibility;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VisibilityChange{
        @NotNull
        private Boolean visibility;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationToAddressRequest{
        @NotNull
        private Double latitude;
        @NotNull
        private Double longitude;
    }
}
