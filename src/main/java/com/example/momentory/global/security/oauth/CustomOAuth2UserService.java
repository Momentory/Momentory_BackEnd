package com.example.momentory.global.security.oauth;

import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String kakaoId = String.valueOf(attributes.get("id"));

        String email = "kakao_" + kakaoId + "@kakao.com";
        String randomPassword = "OAUTH_USER_" + UUID.randomUUID();

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        String encodedPassword = passwordEncoder.encode(randomPassword);


        if (optionalUser.isEmpty()) {
            // User가 없다면 새로 생성 (자동 저장)
            User user = User.builder()
                    .email(email)
                    .name("소셜사용자")
                    .password(encodedPassword)
                    .nickname("kakao_" + kakaoId)
                    .kakaoId(kakaoId)
                    .isActive(true)
                    .build();

            userRepository.save(user);
        }

        return new CustomOAuth2User(kakaoId, oAuth2User);
    }



}