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
        private String backgroundImageName;
        private String backgroundImageUrl;
        private String bio;
        private String externalLink;
        private Long followerCount; // 팔로워 수
        private Long followingCount; // 팔로잉 수
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
        private String backgroundImageName;
        private String backgroundImageUrl;
        private String bio;
        private String externalLink;
        private Long followerCount; // 팔로워 수
        private Long followingCount; // 팔로잉 수
        private Boolean isFollowing; // 현재 사용자가 이 사용자를 팔로우하는지 여부
    }

    // 커뮤니티용 프로필 DTO (간소화된 버전)
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommunityProfileDto {
        private String nickname;
        private String imageUrl; // 프로필사진 URL만
        private String backgroundImageUrl; // 배경사진 URL만
        private String bio;
        private String externalLink;
        private Long followerCount;
        private Long followingCount;
        private Boolean isFollowing; // 다른 사용자 조회 시에만 사용 (내 프로필 조회 시 null)
    }
}
