package com.example.momentory.domain.auth.converter;

import com.example.momentory.domain.auth.dto.AuthRequestDTO;
import com.example.momentory.domain.auth.dto.AuthResponseDTO;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;

public class AuthConverter {

    public static UserRegistrationData toUser(AuthRequestDTO.SignRequestDTO signRequestDTO) {
        User user = User.builder()
                .email(signRequestDTO.getEmail())
                .name(signRequestDTO.getName())
                .password(signRequestDTO.getPassword())
                .nickname(signRequestDTO.getNickName())
                .build();

        UserProfile userProfile = UserProfile.builder()
                .nickname(signRequestDTO.getNickName())
                .phone(signRequestDTO.getPhone())
                .gender(signRequestDTO.getGender())
                .birth(signRequestDTO.getBirthDate())
                .imageName(signRequestDTO.getImageName())
                .imageUrl(signRequestDTO.getImageUrl())
                .bio(signRequestDTO.getBio())
                .externalLink(signRequestDTO.getExternalLink())
                .point(0) // 기본값
                .level(1) // 기본값
                .build();

        return new UserRegistrationData(user, userProfile);
    }

    public static AuthResponseDTO.SignResponseDTO toSigninResponseDTO(User user) {
        return AuthResponseDTO.SignResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static class UserRegistrationData {
        private final User user;
        private final UserProfile userProfile;

        public UserRegistrationData(User user, UserProfile userProfile) {
            this.user = user;
            this.userProfile = userProfile;
        }

        public User user() {
            return user;
        }

        public UserProfile userProfile() {
            return userProfile;
        }
    }
}