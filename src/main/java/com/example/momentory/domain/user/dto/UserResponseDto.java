package com.example.momentory.domain.user.dto;

import com.example.momentory.domain.user.entity.Gender;
import lombok.*;

import java.time.LocalDate;

public class UserResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MyInfoDto {
        private Long userId;
        private String email;
        private String name;
        private String nickname; // User 엔티티의 nickname
        private String phone;
        private LocalDate birth;
        private Gender gender;
        private int point;
        private int level;
        private String imageName;
        private String imageUrl;
        private String bio;
        private String externalLink;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProfileDto {
        private Long userId;
        private String nickname;
        private Gender gender;
        private int point;
        private int level;
        private String imageName;
        private String imageUrl;
        private String bio;
        private String externalLink;
    }
}
