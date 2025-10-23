package com.example.momentory.domain.auth.service;

import com.example.momentory.domain.auth.entity.RefreshToken;
import com.example.momentory.domain.auth.entity.UserTerms;
import com.example.momentory.domain.auth.entity.TermsType;
import com.example.momentory.domain.auth.repository.RefreshTokenRepository;
import com.example.momentory.domain.auth.repository.UserTermsRepository;
import com.example.momentory.domain.auth.converter.AuthConverter;
import com.example.momentory.domain.auth.dto.AuthRequestDTO;
import com.example.momentory.domain.auth.dto.AuthResponseDTO;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import com.example.momentory.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserTermsRepository userTermsRepository;
    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDTO.SignResponseDTO signUp(AuthConverter.UserRegistrationData data, boolean agreeTerms) {
        if (!agreeTerms) {
            throw new GeneralException(ErrorStatus.TERMS_NOT_AGREED);
        }
        
        validatePassword(data.user().getPassword());
        validateEmail(data.user().getEmail());

        String encodedPassword = passwordEncoder.encode(data.user().getPassword());
        data.user().changePassword(encodedPassword);

        try {
            User savedUser = userRepository.save(data.user());
            UserProfile userProfile = data.userProfile();
            userProfile.setUser(savedUser);
            userProfileRepository.save(userProfile);

            // UserTerms 저장 (모든 약관 타입에 대해 동의 처리)
            for (TermsType termsType : TermsType.values()) {
                UserTerms userTerms = UserTerms.builder()
                        .user(savedUser)
                        .termsType(termsType)
                        .agreed(true)
                        .build();
                userTermsRepository.save(userTerms);
            }

            return AuthConverter.toSigninResponseDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            String rootMsg = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();

            if (rootMsg != null) {
                if (rootMsg.contains("uk_user_email")) {
                    throw new GeneralException(ErrorStatus.EMAIL_DUPLICATE);
                } else if (rootMsg.contains("uk_user_nickname")) {
                    throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATE);
                }

                throw new GeneralException(ErrorStatus.DATABASE_ERROR);
            }
        }
        return null;//도달하지 않는 코드
    }


    public void validatePassword(String password) {
        // 길이 검사
        if (password.length() < 8 || password.length() > 12) {
            throw new GeneralException(ErrorStatus.PASSWORD_VALIDATION_FAILED);
        }

        // 영어 대문자, 소문자, 숫자 포함 여부 검사
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        // 최소 2종류 이상 포함 여부 확인
        int count = 0;
        if (hasUpperCase) count++;
        if (hasLowerCase) count++;
        if (hasDigit) count++;

        if (count < 2) {
            throw new GeneralException(ErrorStatus.PASSWORD_VALIDATION_FAILED);
        }
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_FOUND));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);
        userRepository.save(user);

    }

    // 이메일 형식 검증
    public void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex))
            throw new GeneralException(ErrorStatus.EMAIL_VALIDATION_FAILED);

    }

    // 이메일 중복 체크
    public void duplicationCheckEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus.EMAIL_DUPLICATE);
        }
    }

    // 닉네임 중복 체크
    public void duplicationCheckNickName(String nickName){
        if (userProfileRepository.existsByNickname(nickName)) {
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATE);
        }
    }

    //로그인
    @Transactional
    public AuthResponseDTO.LoginResponseDTO login(AuthRequestDTO.LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.PASSWORD_FAILED));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.PASSWORD_FAILED);
        }

        // 탈퇴한 사용자인지 확인
        if (!user.isActive()) {
            throw new GeneralException(ErrorStatus.INACTIVE_USER);
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        // 기존 리프레시 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(RefreshToken.of(
                user.getId(),
                refreshToken,
                LocalDateTime.now().plusDays(7)
        ));

        return new AuthResponseDTO.LoginResponseDTO(user.getId(), accessToken, refreshToken);
    }

    // 토큰재발급
    public Map<String, String> reissueAccessToken(String refreshToken) {

        Long userId = tokenProvider.extractUserIdFromToken(refreshToken);

        RefreshToken saved = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        if (!saved.getRefreshToken().equals(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String newAccessToken = tokenProvider.generateAccessToken(user);

        return Map.of("accessToken", newAccessToken);
    }


    //로그아웃
    @Transactional
    public void logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        System.out.println("UserId  = " + userId);
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public AuthResponseDTO.SignResponseDTO setProfile(AuthRequestDTO.KakaoRequestDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

            user.updateName(dto.getName());
            if (dto.getBio() != null) {
                user.updateBio(dto.getBio());
            }

            Optional<UserProfile> existingByNickName = userProfileRepository.findByNickname(dto.getNickName());
            if (existingByNickName.isPresent() && !existingByNickName.get().getUser().getId().equals(userId)) {
                throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATE);
            }

            Optional<UserProfile> optionalProfile = userProfileRepository.findByUser(user);

            if (optionalProfile.isPresent()) {
                UserProfile profile = optionalProfile.get();
                profile.updateProfile(dto.getNickName(), dto.getBirthDate(), dto.getGender(), dto.getBio(), dto.getImageName(), dto.getImageUrl(), dto.getExternalLink());
            } else {
                UserProfile userProfile = UserProfile.builder()
                        .user(user)
                        .nickname(dto.getNickName())
                        .gender(dto.getGender())
                        .birth(dto.getBirthDate())
                        .imageName(dto.getImageName())
                        .imageUrl(dto.getImageUrl())
                        .externalLink(dto.getExternalLink())
                        .build();

                userProfileRepository.save(userProfile);
            }

            return AuthConverter.toSigninResponseDTO(user);

        } catch (DataIntegrityViolationException e) {
            String rootMsg = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();

            if (rootMsg != null) {
                if (rootMsg.contains("uk_user_email")) {
                    throw new GeneralException(ErrorStatus.EMAIL_DUPLICATE);
                } else if (rootMsg.contains("uk_user_nick_name")) {
                    throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATE);
                }
            }

            throw new GeneralException(ErrorStatus.DATABASE_ERROR);
        }
    }



}
