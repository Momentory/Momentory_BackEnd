package com.example.momentory.domain.auth.dto;

import com.example.momentory.domain.character.entity.status.CharacterType;
import com.example.momentory.domain.user.entity.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class AuthRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignRequestDTO{
        // User 엔티티 필드들
        private String email;
        private String name;
        private String password;
        private String nickName;
        
        // UserProfile 엔티티 필드들
        private String phone;
        private Gender gender;
        private LocalDate birthDate;
        private String imageName;
        private String imageUrl;
        private String bio;
        private String externalLink;
        private boolean agreeTerms;
        private CharacterType characterType; // 회원가입 시 선택할 캐릭터 타입

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequestDTO{
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefreshRequestDTO {
        private String refreshToken;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoRequestDTO {
        // 필수 필드
        private String name;
        private Gender gender;
        private LocalDate birthDate;

        @JsonProperty("nickname")
        private String nickName;

        private CharacterType characterType;

        // 선택 필드
        private String imageName;
        private String imageUrl;
        private String bio;
        private String externalLink;
        private String kakaoNickname;  // 카카오에서 가져온 닉네임
    }

}
