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
        private Double latitude;
        private Double longitude;
        private String address;
        private Boolean visibility;
        private String memo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PhotoUpdate{
        private Double latitude;
        private Double longitude;
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
}
