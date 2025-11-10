package com.example.momentory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProfileDto {
        private String nickName;
        private String imageName;
        private String imageUrl;
        private String backgroundImageName;
        private String backgroundImageUrl;
        private String bio;
        private String externalLink;
    }
}
