package com.example.momentory.domain.auth.dto;

import com.example.momentory.domain.character.entity.CharacterType;
import com.example.momentory.domain.user.entity.Gender;
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
        private String name;
        private Gender gender;
        private LocalDate birthDate;
        private String nickName;
        private String imageName;
        private String imageUrl;
        private String bio;
        private String externalLink;
    }

}
