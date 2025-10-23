package com.example.momentory.domain.auth.dto;

import lombok.*;

@Builder
public class AuthResponseDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignResponseDTO{
        private Long id;
        private String email;
        private String name;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResponseDTO {
        private Long id;
        private String accessToken;
        private String refreshToken;
    }


}
